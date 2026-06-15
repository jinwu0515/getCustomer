# AI 获客演示站 + 后台管理

这是一个面向本地门店和服务型商家的 AI 获客演示项目。前台用于展示获客落地页和线索提交表单，后台用于管理线索、生成 AI 跟进话术，并把生成过的话术保存到数据库中，避免每次重复请求 AI。

GitHub 仓库：

```text
https://github.com/jinwu0515/getCustomer.git
```

## 功能概览

- 前台获客落地页
- 客户咨询表单
- 线索意向评分
- 管理员登录后台
- 线索列表和跟进状态管理
- Qwen AI 跟进话术生成
- AI 话术缓存到数据库
- 后台 AI 话术库
- 已保存话术查看和重新生成

## 项目结构

```text
.
├── admin/demo                 # Spring Boot 后端
│   ├── src/main/java           # 后端业务代码
│   └── src/main/resources      # 配置文件和 SQL
├── vue                        # Vue 3 + Vite 前端
│   ├── src                    # 前端页面和 API 调用
│   ├── assets                 # 样式和图片资源
│   └── tests                  # 前端规则测试
└── README.md
```

## 技术栈

前端：

- Vue 3
- Vite
- Element Plus

后端：

- Java 17
- Spring Boot 3
- MyBatis-Plus
- MySQL

AI：

- Qwen / 通义千问
- DashScope OpenAI-compatible API

## 环境要求

- Node.js
- Java 17
- MySQL
- Qwen API Key，建议配置为 `DASHSCOPE_API_KEY`

## 数据库配置

默认数据库名：

```text
getcustomer
```

后端默认连接配置在 [application.properties](admin/demo/src/main/resources/application.properties)：

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/getcustomer?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:123456}
```

如果你的 MySQL 账号或密码不同，可以在启动后端前设置环境变量：

```bash
DB_USERNAME=你的用户名
DB_PASSWORD=你的密码
```

也可以直接设置完整连接地址：

```bash
DB_URL=jdbc:mysql://localhost:3306/getcustomer?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
```

## 数据表

项目会使用两类核心数据：

- `lead_consultations`：客户咨询线索
- `ai_follow_up_scripts`：AI 生成的话术缓存

AI 话术表 SQL 在：

```text
admin/demo/src/main/resources/sql/ai-follow-up-scripts.sql
```

后端启动时会自动执行这份 SQL：

```properties
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:sql/ai-follow-up-scripts.sql
```

如果数据库账号没有建表权限，或者你想手动建表，也可以在 Navicat 中执行 `ai-follow-up-scripts.sql`。

## Qwen 配置

密钥不写在代码里。后端会从环境变量读取：

```properties
qwen.api-key=${DASHSCOPE_API_KEY:${QWEN_API_KEY:}}
qwen.base-url=${QWEN_BASE_URL:https://dashscope.aliyuncs.com/compatible-mode/v1}
qwen.model=${QWEN_MODEL:qwen-plus}
```

推荐设置：

```bash
DASHSCOPE_API_KEY=你的通义千问 API Key
```

可选配置：

```bash
QWEN_API_KEY=你的通义千问 API Key
QWEN_MODEL=qwen-plus
QWEN_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
```

前端不会接触 Qwen 密钥，只调用后端接口。

## 启动后端

进入后端目录：

```bash
cd admin/demo
```

Windows：

```bash
mvnw.cmd spring-boot:run
```

macOS / Linux：

```bash
./mvnw spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

## 启动前端

进入前端目录：

```bash
cd vue
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

后台地址：

```text
http://localhost:5173/admin
```

如果后端地址不是 `http://localhost:8080`，可以在 `vue` 目录下创建 `.env.local`：

```properties
VITE_API_BASE_URL=http://localhost:8080
```

## 管理员后台

后台入口：

```text
http://localhost:5173/admin
```

默认账号：

```text
账号：admin
密码：admin
```

后台包含两个页面：

- `线索管理`：查看线索、修改跟进状态、生成或读取 AI 跟进话术
- `AI 话术库`：查看已保存的话术、查看完整内容、重新生成并更新数据库

## AI 话术逻辑

在线索管理中点击 `查看/生成`：

1. 后端先根据 `leadId` 查询 `ai_follow_up_scripts`
2. 如果已经有话术，直接返回数据库内容
3. 如果没有话术，调用 Qwen 生成
4. 生成结果保存到 `ai_follow_up_scripts`
5. 下次再点同一条线索时直接读数据库

在 AI 话术库中点击 `重新生成`：

1. 后端根据话术 ID 找到原话术记录
2. 根据 `leadId` 查询对应线索
3. 重新调用 Qwen
4. 用新话术更新原来的数据库记录
5. 前端刷新列表并展示新话术

## 主要接口

线索：

```text
POST  /api/leads
GET   /api/leads
PATCH /api/leads/{id}/follow-status
```

AI 话术：

```text
POST /api/ai/follow-up-script
GET  /api/ai/follow-up-scripts
POST /api/ai/follow-up-scripts/{id}/regenerate
```

管理员登录：

```text
GET  /api/admin/login-public-key
POST /api/admin/login
```

## 常用命令

前端测试：

```bash
cd vue
npm test
```

前端生产构建：

```bash
cd vue
npm run build
```

后端测试：

```bash
cd admin/demo
mvnw.cmd test
```

## 注意事项

- 不要把 Qwen API Key 写进代码或提交到 GitHub。
- 本地开发建议使用环境变量配置 `DASHSCOPE_API_KEY`。
- 如果 `ai_follow_up_scripts` 表没有自动创建，可以手动在 Navicat 执行 `admin/demo/src/main/resources/sql/ai-follow-up-scripts.sql`。
- 如果 Maven Wrapper 首次运行失败，通常是网络无法下载 Maven 分发包，需要先保证网络可访问 Maven 下载地址。
- 如果前端常规构建清理 `dist` 时遇到 Windows 文件占用，可以关闭正在占用 `dist` 资源的进程后重试。
