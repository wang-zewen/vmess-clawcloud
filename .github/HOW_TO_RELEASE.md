# 如何创建Release

本项目使用GitHub Actions自动构建和发布JAR文件。

## 🔄 自动构建（每次push）

每次push到任何分支时，都会自动：
- 构建Java JAR文件
- 上传JAR到GitHub Actions Artifacts（保留90天）
- 构建并推送Docker镜像（仅main/master分支）

## 📦 创建Release

有两种方式创建GitHub Release：

### 方式1：通过Git Tag（推荐）

```bash
# 创建tag
git tag v1.0.0

# 推送tag到远程
git push origin v1.0.0
```

这会自动触发release工作流，创建GitHub Release并附带JAR文件。

### 方式2：手动触发

1. 访问 [Actions页面](https://github.com/wang-zewen/vmess-clawcloud/actions)
2. 选择 "Create Release" 工作流
3. 点击 "Run workflow" 按钮
4. 选择要发布的分支
5. 点击 "Run workflow" 确认

## 📝 版本命名规范

建议使用语义化版本号：
- `v1.0.0` - 主要版本
- `v1.1.0` - 次要版本（新功能）
- `v1.0.1` - 补丁版本（bug修复）
- `v1.0.0-beta` - 预发布版本

## 🎯 Release内容

每个Release会包含：
- `vmess-server.jar` - 标准可执行JAR
- `vmess-server-YYYYMMDD-<hash>.jar` - 带版本标识的JAR
- 详细的版本信息和使用说明
- 自动生成的更新日志

## 📥 下载JAR文件

用户可以从以下位置下载：

1. **GitHub Releases**（推荐）
   - 访问：https://github.com/wang-zewen/vmess-clawcloud/releases
   - 下载最新版本的JAR文件

2. **GitHub Actions Artifacts**
   - 访问：https://github.com/wang-zewen/vmess-clawcloud/actions
   - 选择最新的workflow运行
   - 下载artifact中的JAR文件
   - 注意：仅保留90天

## 🔧 示例：发布新版本

```bash
# 1. 确保在main分支且代码已更新
git checkout main
git pull origin main

# 2. 创建并推送tag
git tag v1.0.0
git push origin v1.0.0

# 3. 等待GitHub Actions完成
# 访问 https://github.com/wang-zewen/vmess-clawcloud/actions 查看进度

# 4. 完成后，在Releases页面查看新创建的release
# https://github.com/wang-zewen/vmess-clawcloud/releases
```

## ❗ 注意事项

- Tag名称必须以 `v` 开头才会触发release工作流
- 不要重复使用相同的tag名称
- Release仅在tag触发时创建，避免频繁创建
- Artifacts在所有分支push时都会创建，用于开发测试
