package com.clawcloud.vmess;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class VmessServer {

    private static final String XRAY_VERSION = "1.8.24";

    private int internalPort;
    private int externalPort;
    private String publicHost;
    private String uuid;
    private String systemArch;

    public static void main(String[] args) {
        try {
            VmessServer server = new VmessServer();
            server.initialize();
            server.downloadXray();
            server.generateConfig();
            server.generateVmessLink();
            server.startXray();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initialize() throws Exception {
        System.out.println("==========================================");
        System.out.println("🚀 VMess Server Starting (Java Version)");
        System.out.println("==========================================");

        // 检测系统架构
        this.systemArch = detectSystemArch();
        System.out.println("💻 System Architecture: " + systemArch);

        // 读取环境变量
        String portEnv = System.getenv("PORT");
        this.internalPort = portEnv != null ? Integer.parseInt(portEnv) : 20219;

        String extPortEnv = System.getenv("EXTERNAL_PORT");
        this.externalPort = extPortEnv != null ? Integer.parseInt(extPortEnv) : this.internalPort;

        String uuidEnv = System.getenv("VMESS_UUID");
        // 默认 UUID，也可以通过环境变量 VMESS_UUID 覆盖
        this.uuid = uuidEnv != null ? uuidEnv : "b752ce97-f1eb-4a58-bdb3-96dd11a72d4d";

        System.out.println("📌 Internal Port (Container): " + internalPort);
        System.out.println("📌 External Port (Public): " + externalPort);
        System.out.println("🔑 UUID: " + uuid);

        // 获取公网地址
        this.publicHost = System.getenv("PUBLIC_HOST");
        if (this.publicHost == null || this.publicHost.isEmpty()) {
            this.publicHost = getPublicIP();
        }

        System.out.println("✅ Public Host: " + publicHost);
    }

    private String getPublicIP() {
        String[] urls = {
            "https://api64.ipify.org",
            "https://ifconfig.me"
        };

        for (String urlStr : urls) {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String ip = reader.readLine();
                    if (ip != null && !ip.isEmpty()) {
                        return ip.trim();
                    }
                }
            } catch (Exception e) {
                // 尝试下一个URL
            }
        }

        return "UNKNOWN";
    }

    private String detectSystemArch() throws Exception {
        String osArch = System.getProperty("os.arch").toLowerCase();

        // 映射 Java 架构名称到 Xray 发布文件名
        if (osArch.contains("amd64") || osArch.contains("x86_64")) {
            return "64";
        } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
            return "arm64-v8a";
        } else if (osArch.contains("arm")) {
            return "arm32-v7a";
        } else {
            throw new Exception("Unsupported architecture: " + osArch);
        }
    }

    private String getXrayDownloadUrl() {
        return String.format(
            "https://github.com/XTLS/Xray-core/releases/download/v%s/Xray-linux-%s.zip",
            XRAY_VERSION,
            systemArch
        );
    }

    private void downloadXray() throws Exception {
        File xrayFile = new File("xray");
        if (xrayFile.exists()) {
            System.out.println("✅ Xray already exists");
            return;
        }

        String downloadUrl = getXrayDownloadUrl();
        System.out.println("📥 Downloading Xray v" + XRAY_VERSION + " for " + systemArch + "...");
        System.out.println("📦 URL: " + downloadUrl);

        // 下载ZIP文件
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);

        try (InputStream in = conn.getInputStream();
             ZipInputStream zis = new ZipInputStream(in)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("xray")) {
                    // 解压xray文件
                    try (FileOutputStream fos = new FileOutputStream("xray")) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    break;
                }
                zis.closeEntry();
            }
        }

        // 设置可执行权限
        xrayFile.setExecutable(true);
        System.out.println("✅ Xray installed");
    }

    private void generateConfig() throws Exception {
        String config = String.format(
            "{\n" +
            "  \"log\": {\"loglevel\": \"none\"},\n" +
            "  \"inbounds\": [\n" +
            "    {\n" +
            "      \"port\": %d,\n" +
            "      \"listen\": \"0.0.0.0\",\n" +
            "      \"protocol\": \"vmess\",\n" +
            "      \"settings\": {\n" +
            "        \"clients\": [{\"id\": \"%s\", \"alterId\": 0}]\n" +
            "      },\n" +
            "      \"streamSettings\": {\n" +
            "        \"network\": \"tcp\",\n" +
            "        \"tcpSettings\": {\n" +
            "          \"acceptProxyProtocol\": false,\n" +
            "          \"header\": {\n" +
            "            \"type\": \"http\",\n" +
            "            \"response\": {\n" +
            "              \"version\": \"1.1\",\n" +
            "              \"status\": \"200\",\n" +
            "              \"reason\": \"OK\",\n" +
            "              \"headers\": {\n" +
            "                \"Content-Type\": [\"text/html; charset=utf-8\"],\n" +
            "                \"Transfer-Encoding\": [\"chunked\"],\n" +
            "                \"Connection\": [\"keep-alive\"],\n" +
            "                \"Pragma\": \"no-cache\"\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"tag\": \"vmess\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"outbounds\": [{\"protocol\": \"freedom\"}]\n" +
            "}",
            internalPort, uuid
        );

        Files.write(Paths.get("c.json"), config.getBytes());
    }

    private void generateVmessLink() throws Exception {
        String vmessJson = String.format(
            "{\"v\":\"2\",\"ps\":\"ClawCloud-VMess-Java\",\"add\":\"%s\",\"port\":\"%d\"," +
            "\"id\":\"%s\",\"aid\":\"0\",\"net\":\"tcp\",\"type\":\"http\",\"tls\":\"\"}",
            publicHost, externalPort, uuid
        );

        String vmessLink = "vmess://" + Base64.getEncoder().encodeToString(vmessJson.getBytes());

        Files.write(Paths.get("link.txt"), vmessLink.getBytes());

        System.out.println();
        System.out.println("==========================================");
        System.out.println("🎉 VMess Server Ready! (Java Version)");
        System.out.println("==========================================");
        System.out.println("📍 Container listens on: 0.0.0.0:" + internalPort);
        System.out.println("📍 Public access: " + publicHost + ":" + externalPort);
        System.out.println("🔑 UUID: " + uuid);
        System.out.println();
        System.out.println("🔗 VMess Link:");
        System.out.println(vmessLink);
        System.out.println();
        System.out.println("💾 Link saved to: link.txt");
        System.out.println("==========================================");
        System.out.println();
        System.out.println("🚀 Starting Xray on port " + internalPort + "...");
        System.out.println();
    }

    private void startXray() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("./xray", "run", "-c", "c.json");
        pb.inheritIO();
        Process process = pb.start();

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Shutting down Xray...");
            process.destroy();
        }));

        // 等待进程结束
        int exitCode = process.waitFor();
        System.exit(exitCode);
    }
}
