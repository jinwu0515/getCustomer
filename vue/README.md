# AI 技术接单第一版启动包

## 里面有什么

- `index.html`：美业门店 AI 获客助手演示站。
- `src/App.vue`：Vue 3 单页应用主体，包含页面结构、表单和结果展示。
- `src/api.js`：调用 Spring Boot 后端接口，保存和读取线索数据。
- `src/pricing.js`：客户意向评分和报价建议规则。
- `src/main.js`：Vue 应用挂载入口。
- `assets/hero-beauty-ai.png`：本地展示图，不依赖外网。
- `sales-playbook.md`：30 秒介绍、私聊开场、报价、每日获客动作。
- `lead-tracker.csv`：200 个潜在客户跟进表的起始模板。
- `case-template.md`：每单交付后沉淀案例的模板。

## 怎么使用

这是一个 Vue 3 + Vite 项目，首次运行先安装依赖：

```bash
npm install
```

本地开发预览：

```bash
npm run dev
```

默认后端地址是 `http://localhost:8080`。如需修改，在 `vue` 目录创建 `.env.local`：

```properties
VITE_API_BASE_URL=http://localhost:8080
```

管理员端页面：

```text
http://localhost:5173/admin
```

默认账号：admin

默认密码：admin

管理员端 UI 使用 Element Plus，登录成功后才能读取线索表和修改跟进状态。

生产构建：

```bash
npm run build
```

报价规则测试：

```bash
npm test
```

## 第一周执行

1. 把演示站发给 20 个身边商家或朋友介绍的商家。
2. 每次只问一个问题：现在客户咨询、报价、预约是不是靠人工反复回复？
3. 对方愿意看演示时，打开报价工具现场演示。
4. 报体验价时优先用 3980 元标准版，收 40% 定金。
5. 每天把沟通结果写进 `lead-tracker.csv`。

## 交付口径

第一版只承诺一件事：把客户从“问一下”推进到“留下需求、预算、到店时间、联系方式”。不要承诺复杂 SaaS、全自动成交或高级 CRM。

## 验证记录

- 报价模块已用 Node 运行环境验证：标准转化包、40% 定金、3980 报价、高意向评分均符合预期。
- Vue 3 项目已通过 `npm test` 和临时输出目录构建验证。
