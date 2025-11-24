# VMess Server - Java JAR 版本

这是VMess Server的Java实现版本，功能与Bash脚本版完全相同。

## 📋 特性

- ✅ 与Bash版本功能完全一致
- ✅ 使用Java 11编写，跨平台支持
- ✅ Maven构建，易于管理依赖
- ✅ 生成可执行JAR文件
- ✅ 支持Docker容器化部署

## 📥 下载预编译JAR（推荐）

**完全自动化发布！** 无需本地构建，可以直接从GitHub获取预编译的JAR文件：

### 从GitHub Releases下载（推荐）

1. 访问 [Releases页面](https://github.com/wang-zewen/vmess-clawcloud/releases)
2. 选择最新的Release版本
3. 下载 `vmess-server.jar`
4. **每次push到main分支都会自动创建新的Release**

### 从GitHub Actions下载（测试版本）

1. 访问 [Actions页面](https://github.com/wang-zewen/vmess-clawcloud/actions)
2. 选择 "Build and Push Docker Image" workflow
3. 选择最新成功的workflow运行
4. 下载artifact中的JAR文件
5. **每次push到任何分支都会自动构建**

## 🔨 构建

如果需要本地构建：

### 前置要求

- Java 11 或更高版本
- Maven 3.6 或更高版本

### 构建步骤

```bash
# 使用Maven构建
mvn clean package

# 或使用构建脚本
./build.sh
```

构建完成后，JAR文件位于：`target/vmess-server.jar`

## 🚀 运行

### 直接运行JAR

```bash
java -jar target/vmess-server.jar
```

### 使用环境变量

```bash
export PORT=8080
export EXTERNAL_PORT=12345
export VMESS_UUID=your-uuid-here
export PUBLIC_HOST=your-domain.com

java -jar target/vmess-server.jar
```

### 使用Docker

```bash
# 构建Docker镜像
docker build -t vmess-server:java .

# 运行容器
docker run -p 80:80 \
  -e EXTERNAL_PORT=12345 \
  -e VMESS_UUID=your-uuid-here \
  vmess-server:java
```

## 📝 环境变量

| 变量名 | 说明 | 默认值 |
|-------|------|--------|
| PORT | 容器内监听端口 | 80 |
| EXTERNAL_PORT | 公网访问端口 | 与PORT相同 |
| VMESS_UUID | VMess UUID | 自动生成 |
| PUBLIC_HOST | 公网访问地址 | 自动获取 |

## 🏗️ 项目结构

```
java-version/
├── pom.xml                           # Maven配置
├── Dockerfile                        # Docker配置
├── build.sh                          # 构建脚本
├── README.md                         # 本文档
└── src/
    └── main/
        └── java/
            └── com/
                └── clawcloud/
                    └── vmess/
                        └── VmessServer.java  # 主程序
```

## 🔍 代码说明

`VmessServer.java` 实现了以下功能：

1. **初始化配置** - 读取环境变量，获取公网IP
2. **下载Xray** - 自动下载并解压Xray二进制文件
3. **生成配置** - 创建Xray配置文件（c.json）
4. **生成链接** - 生成VMess订阅链接
5. **启动服务** - 启动Xray代理服务

## 🆚 与Bash版本对比

**优势：**
- 更好的跨平台支持
- 适合Java生态系统集成
- 更容易进行单元测试
- 更强的类型安全

**劣势：**
- 镜像体积更大（需要JRE）
- 启动速度稍慢
- 需要Java运行环境

## 📦 发布

构建的JAR文件可以直接发布到：
- Maven仓库（私有/公共）
- GitHub Releases
- Docker Registry

## 🐛 调试

启用详细日志：

```bash
java -Djava.util.logging.config.file=logging.properties -jar target/vmess-server.jar
```

## 📄 许可

与主项目相同
