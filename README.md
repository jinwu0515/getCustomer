# AI 获客演示站 + 后台管理

## 项目结构

- `vue`：Vue 3 前端，包含获客落地页、咨询表单和线索后台表格。
- `admin/demo`：Spring Boot 3 后端，使用 MyBatis-Plus 写入和读取 MySQL 数据。

## 数据库

数据库名：`getcustomer`

表名：`lead_consultations`

后端默认连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/getcustomer?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=
```

如果你的 MySQL 账号不是默认值，可以启动后端前设置环境变量：

```bash
DB_USERNAME=你的用户名
DB_PASSWORD=你的密码
```

## 启动后端

```bash
cd admin/demo
./mvnw spring-boot:run
```

Windows：

```bash
cd admin/demo
mvnw.cmd spring-boot:run
```

后端接口地址：

- `POST http://localhost:8080/api/leads`：保存用户填写的数据
- `GET http://localhost:8080/api/leads`：查看线索列表
- `PATCH http://localhost:8080/api/leads/{id}/follow-status`：更新跟进状态

## 启动前端

```bash
cd vue
npm install
npm run dev
```

前端默认请求后端：

```text
http://localhost:8080
```

如需改后端地址，在 `vue` 目录下创建 `.env.local`：

```properties
VITE_API_BASE_URL=http://localhost:8080
```

## 管理员端

管理员页面：

```text
http://localhost:5173/admin
```

默认账号：

```text
账号：admin
密码：admin
```

管理员登录后才能查看和修改线索数据。前台页面仍然可以直接提交线索，提交的数据会写入 `lead_consultations` 表。

管理员端 UI 使用 Element Plus，因此 `vue/package.json` 中包含：

```json
"element-plus": "^2.11.3"
```
