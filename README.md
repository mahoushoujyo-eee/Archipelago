# Archipelago Monorepo

一个包含后端微服务与前端应用的多项目仓库，支持用户认证、博客、资源管理、AI/RAG 与网关聚合。

## 项目结构

- `account/` 用户认证与权限（Spring Security、OAuth2、JWT）
- `blog/` 博客服务（文章、评论、点赞）
- `resource/` 资源服务（OSS 上传、下载、统计）
- `ai/` AI/RAG 服务（知识库与向量检索）
- `gateway/` API 网关（路由聚合、权限过滤）
- `frontend/` 前端应用（Vue 3 + Vite + Element Plus）

## 端口与路由

- `gateway` 默认端口：`57070`，路由文件：`src/main/resources/application-route.yml`
  - 公开路由示例：`/api/blogs/**`、`/api/resources/oss/public/**`
  - 受保护路由示例：`/api/resources/**`、`/api/llm/**`
- 其他服务（依据配置文件）：
  - `account-service`：`58080`
  - `blog-service`：`58050`
  - `resource-service`：`58012`
- 前端开发端口：`frontend` 使用 Vite 默认 `5000`

## 快速开始

1. 准备依赖
   - JDK 17+、Maven（或使用各项目内的 `mvnw`）
   - Node.js 16+（前端）
   - Nacos 注册中心（示例地址：`127.0.0.1:13848`）
   - MySQL 数据库（示例库：`spring_blog`）
2. 启动后端服务（每个子项目）
   - 进入对应 `xxx-service` 目录，配置数据库、注册中心等，再执行：
     - `mvn spring-boot:run` 或
     - `mvn clean package && java -jar target/*.jar`
3. 启动前端
   - 进入 `new-blog/`：`npm install && npm run dev`
   - 前端通过代理访问后端：`/api`（认证）、`/blogs`（博客）、`/resources`（资源）、`/llm`（AI，经网关转发）

## 配置与环境变量建议

为避免敏感信息提交到仓库，建议：

- 所有服务的密钥与凭据使用环境变量或独立未纳入版本控制的配置文件（例如 `application-local.yaml`）。
- 提交示例文件（如 `application-example.yaml`）而非真实凭据。
- 推荐的环境变量（示例）：
  - `ACCOUNT_GITHUB_CLIENT_ID`、`ACCOUNT_GITHUB_CLIENT_SECRET`
  - `SPRING_MAIL_USERNAME`、`SPRING_MAIL_PASSWORD`
  - `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER`、`DB_PASSWORD`
  - `ALIYUN_OSS_ACCESS_KEY_ID`、`ALIYUN_OSS_ACCESS_KEY_SECRET`
  - `JWT_SECRET`

## 开发规范

- 不在代码与版本控制中存储密钥、令牌、密码等敏感信息。
- 日志中避免打印凭据或用户密码（认证过滤器、异常处理等场景）。
- 统一在根 `.gitignore` 层忽略 `*.yaml/*.yml/*.properties` 等配置文件（你已启用）。

## 安全提示

- 如果仓库中已提交过敏感信息，请尽快：
  - 进行密钥与密码的轮换（更换为新值，旧值作废）。
  - 清理 Git 历史（如需），保证公开仓库不再可见旧凭据。
  - 改为从环境变量或安全配置源加载。

## 许可与致谢

- 前端与后端组件参考各子项目内的说明与依赖。
- 如需对外开源，建议补充统一许可证与贡献指南。