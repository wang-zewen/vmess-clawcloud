#!/bin/bash
set -e
# ==================== 配置 ====================
# 容器内监听端口（从环境变量读取，默认 80）
INTERNAL_PORT=${PORT:-80}
# 公网访问端口（从环境变量获取，默认等于内部端口）
EXTERNAL_PORT=${EXTERNAL_PORT:-${INTERNAL_PORT}}
# 公网访问地址（从环境变量获取）
PUBLIC_HOST=${PUBLIC_HOST:-""}
# UUID（从环境变量获取，否则随机生成）
UUID=${VMESS_UUID:-$(cat /proc/sys/kernel/random/uuid 2>/dev/null || uuidgen)}
V=1.8.24

echo "=========================================="
echo "🚀 VMess Server Starting"
echo "=========================================="
echo "📌 Internal Port (Container): $INTERNAL_PORT"
echo "📌 External Port (Public): $EXTERNAL_PORT"
echo "🔑 UUID: $UUID"

# ==================== 获取公网地址 ====================
# 如果没有设置 PUBLIC_HOST，则自动获取
if [ -z "$PUBLIC_HOST" ]; then
    PUBLIC_HOST=$(curl -s --connect-timeout 3 https://api64.ipify.org||curl -s --connect-timeout 3 https://ifconfig.me||echo "UNKNOWN")
fi

echo "✅ Public Host: $PUBLIC_HOST"

# ==================== 下载 Xray ====================
if [ ! -f xray ]; then
    echo "📥 Downloading Xray v${V}..."
    curl -sLo x.zip https://github.com/XTLS/Xray-core/releases/download/v${V}/Xray-linux-64.zip
    unzip -qo x.zip xray
    chmod +x xray
    rm x.zip
    echo "✅ Xray installed"
fi

# ==================== 生成 Xray 配置 ====================
cat > c.json << EOF
{
  "log": {"loglevel": "none"},
  "inbounds": [
    {
      "port": ${INTERNAL_PORT},
      "listen": "0.0.0.0",
      "protocol": "vmess",
      "settings": {
        "clients": [{"id": "${UUID}", "alterId": 0}]
      },
      "streamSettings": {
        "network": "tcp",
        "tcpSettings": {
          "acceptProxyProtocol": false,
          "header": {
            "type": "http",
            "response": {
              "version": "1.1",
              "status": "200",
              "reason": "OK",
              "headers": {
                "Content-Type": ["text/html; charset=utf-8"],
                "Transfer-Encoding": ["chunked"],
                "Connection": ["keep-alive"],
                "Pragma": "no-cache"
              }
            }
          }
        }
      },
      "tag": "vmess"
    }
  ],
  "outbounds": [{"protocol": "freedom"}]
}
EOF

# ==================== 生成 VMess 链接 ====================
# VMess 链接使用公网端口和地址
L="vmess://$(echo -n "{\"v\":\"2\",\"ps\":\"ClawCloud-VMess\",\"add\":\"$PUBLIC_HOST\",\"port\":\"$EXTERNAL_PORT\",\"id\":\"$UUID\",\"aid\":\"0\",\"net\":\"tcp\",\"type\":\"http\",\"tls\":\"\"}"|base64 -w 0)"
echo "$L" > link.txt

echo ""
echo "=========================================="
echo "🎉 VMess Server Ready!"
echo "=========================================="
echo "📍 Container listens on: 0.0.0.0:$INTERNAL_PORT"
echo "📍 Public access: $PUBLIC_HOST:$EXTERNAL_PORT"
echo "🔑 UUID: $UUID"
echo ""
echo "🔗 VMess Link:"
echo "$L"
echo ""
echo "💾 Link saved to: link.txt"
echo "=========================================="
echo ""
echo "🚀 Starting Xray on port $INTERNAL_PORT..."
echo ""

exec ./xray run -c c.json
```
