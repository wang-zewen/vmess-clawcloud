# VMess Server for Claw Cloud

一键部署 VMess 服务器到 Claw Cloud，支持 TCP 传输协议。

## 📋 功能特性

- ✅ 自动构建 Docker 镜像 (GitHub Actions)
- ✅ 支持 mKCP/UDP 传输（适配 Claw Cloud UDP 端口）
- ✅ 自动生成 VMess 链接
- ✅ 环境变量配置，灵活部署
- ✅ 无日志输出，保护隐私
- ✅ 开箱即用

## 🚀 快速开始

在Claw Cloud 创建应用，镜像选择公共，镜像名称填写`
ghcr.io/wang-zewen/vmess-clawcloud:latest`



Network 选择默认的80，修改为tcp就可以了，一个端口足矣！！！

第一次不用填环境变量

部署成功看到分配的端口号后，设置环境变量（也可以不设置，直接去到使用的订阅链接软件中，右键编辑，将端口改为分配的端口就行，其余不变）

**环境变量配置：**

点击 **Environment Variables** → **Add**，输入：
```
VMESS_UUID=你的UUID (Optional,不写就是自动生成UUID)
EXTERNAL_PORT=Claw Cloud分配的公网端口
```



> 💡 **提示**: 如果不设置 `VMESS_UUID`，每次重启会生成新的 UUID

### 5. 获取 VMess 链接

部署成功后，在 Claw Cloud 的日志中查找：
```
🔗 VMess Link:
vmess://eyJ2IjoiMiIsInBzIjoi...
```

复制这个链接到你的 V2Ray 客户端即可使用。


---

**Made with ❤️ for Claw Cloud**
