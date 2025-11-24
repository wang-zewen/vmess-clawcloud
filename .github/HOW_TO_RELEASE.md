# 自动发布说明

本项目使用GitHub Actions**完全自动**构建和发布JAR文件，无需手动操作。

## 🔄 自动构建（每次push）

### 任何分支push时

- ✅ 自动构建Java JAR文件
- ✅ 上传JAR到GitHub Actions Artifacts（保留90天）
- 📦 适合开发和测试使用

### Push到main/master分支时

在上述基础上，额外执行：
- ✅ 自动创建GitHub Release
- ✅ 附带JAR文件下载
- ✅ 生成版本号：`v1.0.0-YYYYMMDD-<git-hash>`
- ✅ 构建并推送Docker镜像
- 🎉 **完全自动，无需手动操作！**

## 📦 Release创建流程

当代码合并到main/master分支时：

1. **自动触发**：GitHub Actions检测到push
2. **构建JAR**：使用Maven编译打包
3. **创建Release**：自动创建新的GitHub Release
4. **上传文件**：
   - `vmess-server.jar` - 标准JAR
   - `vmess-server-YYYYMMDD-<hash>.jar` - 带版本号的JAR
5. **构建镜像**：同时构建Docker镜像

**你不需要做任何事情！**只需要将代码合并到main分支即可。

## 🎯 Release内容

每个自动创建的Release包含：
- ✅ `vmess-server.jar` - 标准可执行JAR
- ✅ `vmess-server-YYYYMMDD-<hash>.jar` - 带版本标识的JAR
- ✅ 详细的构建信息（日期、commit、分支）
- ✅ 完整的使用说明
- ✅ 自动生成的更新日志

## 📥 用户下载JAR文件

用户可以从以下位置下载：

### 方式1：GitHub Releases（推荐）
- 访问：https://github.com/wang-zewen/vmess-clawcloud/releases
- 选择最新的Release
- 下载 `vmess-server.jar`
- **适合生产环境使用**

### 方式2：GitHub Actions Artifacts
- 访问：https://github.com/wang-zewen/vmess-clawcloud/actions
- 选择 "Build and Push Docker Image" workflow
- 选择最新成功的运行
- 下载 artifact 中的JAR文件
- **适合测试最新代码**
- 注意：仅保留90天

## 🚀 开发流程示例

```bash
# 1. 开发功能
git checkout -b feature/new-feature
# ... 开发和提交 ...

# 2. 合并到main（通过PR或直接push）
git checkout main
git merge feature/new-feature
git push origin main

# 3. 自动发生：
# - GitHub Actions自动构建
# - 自动创建Release
# - JAR文件自动发布
# - Docker镜像自动推送

# 4. 几分钟后，访问Releases页面即可看到新版本
```

## 📊 版本号规则

自动生成的版本号格式：`v1.0.0-YYYYMMDD-<git-hash>`

示例：
- `v1.0.0-20250124-abc1234`
  - 基础版本：v1.0.0
  - 构建日期：2025年1月24日
  - Git提交：abc1234

这确保了每次发布都有唯一的版本号，不会冲突。

## ❗ 注意事项

- ✅ **完全自动**：无需手动创建tag或release
- ✅ **每次push到main都会创建新release**：适合快速迭代
- ✅ **版本号自动生成**：避免冲突
- ✅ **并行构建**：JAR、Docker镜像同时构建
- 📝 如果不想创建release，push到其他分支即可（只会创建artifact）
