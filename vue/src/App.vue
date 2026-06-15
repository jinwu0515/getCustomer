<script setup>
import { computed, reactive, ref } from "vue";
import AdminPage from "./AdminPage.vue";
import { createLead } from "./api.js";
import { buildLeadPlan, formOptions } from "./pricing.js";

const navItems = [
  { label: "套餐", href: "#offer" },
  { label: "报价工具", href: "#tool" },
  { label: "交付", href: "#timeline" },
  { label: "后台", href: "/admin" },
];

const trustItems = ["3-7 天交付", "先收 40% 定金", "可沉淀案例"];

const packages = [
  {
    tag: "第一单优先",
    price: "2980",
    title: "基础获客包",
    description: "门店介绍页、客户留资表单、常见问题回复，适合快速拿下第一批客户。",
  },
  {
    tag: "主推套餐",
    price: "3980-5980",
    title: "标准转化包",
    description: "增加客户意向评分、报价建议、自动跟进话术和客户资料整理。",
  },
  {
    tag: "复购升级",
    price: "12800+",
    title: "增长交付包",
    description: "把预约、成交复盘、数据表和 AI 工作流组合成行业解决方案。",
  },
];

const timelineSteps = [
  {
    title: "需求确认",
    description: "收集门店资料、套餐价格、常见问题和预约方式。",
  },
  {
    title: "页面初稿",
    description: "完成落地页、表单字段、咨询问题和基础文案。",
  },
  {
    title: "工具联调",
    description: "配置报价规则、意向评分、跟进话术和资料导出。",
  },
  {
    title: "上线复盘",
    description: "交付截图、使用说明、维护包报价和转介绍请求。",
  },
];

const form = reactive({
  service: "皮肤管理",
  budget: "3000-6000",
  urgency: "本周",
  customerType: "新客",
  channel: "小红书",
});

const hasSubmitted = ref(true);
const defaultFeaturedPackageIndex = 1;
const activePackageIndex = ref(defaultFeaturedPackageIndex);
const isSaving = ref(false);
const saveMessage = ref("");
const saveError = ref("");
const plan = computed(() => buildLeadPlan(form));

const isAdminRoute = computed(() => window.location.pathname === "/admin");

const leadPayload = computed(() => ({
  serviceName: form.service,
  budgetRange: form.budget,
  urgency: form.urgency,
  customerType: form.customerType,
  sourceChannel: form.channel,
}));

async function generatePlan() {
  hasSubmitted.value = true;
  isSaving.value = true;
  saveMessage.value = "";
  saveError.value = "";

  try {
    await createLead(leadPayload.value);
    saveMessage.value = "已保存到后台线索表";
  } catch (error) {
    saveError.value = error.message || "保存失败，请确认后端服务和 MySQL 已启动";
  } finally {
    isSaving.value = false;
  }
}
</script>

<template>
  <AdminPage v-if="isAdminRoute" />

  <template v-else>
  <header class="topbar">
    <a class="brand" href="#top" aria-label="萤火获客系统首页">
      <span class="brand-mark">AI</span>
      <span>
        <strong>萤火获客系统</strong>
        <small>美业门店转化工具</small>
      </span>
    </a>
    <nav aria-label="页面导航">
      <a v-for="item in navItems" :key="item.href" :href="item.href">
        {{ item.label }}
      </a>
    </nav>
  </header>

  <main id="top">
    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">AI 营销落地页 + 咨询报价小工具</p>
        <h1>把门店咨询，从反复回复变成自动留资和预约。</h1>
        <p class="lead">
          给小微商家看的成交样板：客户扫码后填写项目、预算、到店时间，系统自动判断意向、生成报价建议和跟进话术。
        </p>
        <div class="hero-actions">
          <a class="button" href="#tool">试用报价工具</a>
          <a class="button secondary" href="./sales-playbook.md">查看获客话术</a>
        </div>
        <div class="trust-strip" aria-label="服务亮点">
          <span v-for="item in trustItems" :key="item">{{ item }}</span>
        </div>
      </div>
      <figure class="hero-visual">
        <img src="../assets/hero-beauty-ai.png" alt="美业门店 AI 获客工具界面展示" />
        <figcaption>
          <strong>演示场景</strong>
          <span>客户从小红书进入页面，留下预算和预约意向，前台直接拿到回复建议。</span>
        </figcaption>
      </figure>
    </section>

    <section class="section intro">
      <div>
        <p class="eyebrow">你卖给客户的结果</p>
        <h2>不是“做个网站”，而是帮门店跑通一条获客流程。</h2>
      </div>
      <p class="section-copy">
        页面、表单、报价规则和跟进话术组合在一起，客户就能看懂价值：少漏掉咨询、优先跟进高意向客户、把成交动作标准化。
      </p>
    </section>

    <section class="section band" id="offer">
      <div class="section-head">
        <p class="eyebrow">报价结构</p>
        <h2>三档套餐，方便你从体验价成交到标准化交付。</h2>
      </div>
      <div class="grid">
        <article
          v-for="(item, index) in packages"
          :key="item.title"
          class="card"
          :class="{ featured: activePackageIndex === index }"
          tabindex="0"
          @mouseenter="activePackageIndex = index"
          @mouseleave="activePackageIndex = defaultFeaturedPackageIndex"
          @focus="activePackageIndex = index"
          @blur="activePackageIndex = defaultFeaturedPackageIndex"
        >
          <span class="tag">{{ item.tag }}</span>
          <p class="metric">{{ item.price }}</p>
          <h3>{{ item.title }}</h3>
          <p class="muted">{{ item.description }}</p>
        </article>
      </div>
    </section>

    <section class="section" id="tool">
      <div class="section-head">
        <p class="eyebrow">现场演示</p>
        <h2>AI 咨询报价助手</h2>
        <p class="muted">把这块展示给商家看，比解释“我会 AI”更有说服力。</p>
      </div>
      <div class="tool">
        <form class="form-panel" @submit.prevent="generatePlan">
          <div class="panel-title">
            <span>01</span>
            <h3>输入客户咨询信息</h3>
          </div>
          <div class="field">
            <label for="service">客户咨询项目</label>
            <input id="service" v-model.trim="form.service" name="service" />
          </div>
          <div class="field">
            <label for="budget">预算区间</label>
            <select id="budget" v-model="form.budget" name="budget">
              <option v-for="option in formOptions.budgets" :key="option">
                {{ option }}
              </option>
            </select>
          </div>
          <div class="field-row">
            <div class="field">
              <label for="urgency">到店时间</label>
              <select id="urgency" v-model="form.urgency" name="urgency">
                <option v-for="option in formOptions.urgencies" :key="option">
                  {{ option }}
                </option>
              </select>
            </div>
            <div class="field">
              <label for="customerType">客户类型</label>
              <select id="customerType" v-model="form.customerType" name="customerType">
                <option v-for="option in formOptions.customerTypes" :key="option">
                  {{ option }}
                </option>
              </select>
            </div>
          </div>
          <div class="field">
            <label for="channel">来源渠道</label>
            <select id="channel" v-model="form.channel" name="channel">
              <option v-for="option in formOptions.channels" :key="option">
                {{ option }}
              </option>
            </select>
          </div>
          <button class="button wide" type="submit" :disabled="isSaving">
            {{ isSaving ? "正在保存..." : "生成报价建议并保存" }}
          </button>
          <p v-if="saveMessage" class="form-message success">{{ saveMessage }}</p>
          <p v-if="saveError" class="form-message error">{{ saveError }}</p>
        </form>

        <aside v-if="hasSubmitted" class="result-panel">
          <div class="result-head">
            <div class="panel-title">
              <span>02</span>
              <h3>咨询处理结果</h3>
            </div>
            <span class="badge">{{ plan.level }}</span>
          </div>
          <div class="score-line">
            <span>意向分</span>
            <strong>{{ plan.score }} 分</strong>
          </div>
          <div class="result-list">
            <div class="result-item">
              <strong>报价建议</strong>
              <span>{{ plan.quote }}</span>
            </div>
            <div class="result-item">
              <strong>自动回复</strong>
              <span>{{ plan.reply }}</span>
            </div>
            <div class="result-item">
              <strong>下一步</strong>
              <span>{{ plan.nextStep }}</span>
            </div>
            <div class="result-item">
              <strong>维护包</strong>
              <span>{{ plan.maintenance }}</span>
            </div>
          </div>
        </aside>
      </div>
    </section>

    <section class="section band" id="timeline">
      <div class="section-head">
        <p class="eyebrow">交付节奏</p>
        <h2>对客户承诺 7 天内看到能用的东西。</h2>
      </div>
      <div class="timeline">
        <article v-for="step in timelineSteps" :key="step.title" class="step">
          <h3>{{ step.title }}</h3>
          <p class="muted">{{ step.description }}</p>
        </article>
      </div>
    </section>

    <section class="section cta">
      <p class="eyebrow">第一周就这样用</p>
      <h2>每天发 20 个商家，现场演示这个页面，收定金后再定制。</h2>
    </section>
  </main>

  <footer class="footer">
    <span>真实交付时替换为客户门店资料、图片、套餐和预约入口。</span>
    <a href="./case-template.md">案例模板</a>
  </footer>
  </template>
</template>
