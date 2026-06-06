<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import {
  clearAdminToken,
  fetchLeads,
  getAdminToken,
  loginAdmin,
  setAdminToken,
  updateLeadFollowStatus,
} from "./api.js";

const loginForm = reactive({
  username: "",
  password: "",
});

const loginRules = {
  username: [{ required: true, message: "请输入管理员账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入管理员密码", trigger: "blur" }],
};

const isLoggedIn = ref(Boolean(getAdminToken()));
const isLoggingIn = ref(false);
const isLoadingLeads = ref(false);
const leads = ref([]);
const activeFollowStatus = ref("全部");

const followStatusOptions = ["待跟进", "已联系", "已成交", "暂不考虑"];
const followStatusFilters = computed(() => ["全部", ...followStatusOptions]);
const highIntentCount = computed(() => leads.value.filter((lead) => lead.intentLevel === "高意向").length);
const statusCounts = computed(() => {
  const counts = Object.fromEntries(followStatusFilters.value.map((status) => [status, 0]));
  counts["全部"] = leads.value.length;

  for (const lead of leads.value) {
    const status = lead.followStatus || "待跟进";
    counts[status] = (counts[status] || 0) + 1;
  }

  return counts;
});
const filteredLeads = computed(() => {
  if (activeFollowStatus.value === "全部") {
    return leads.value;
  }

  return leads.value.filter((lead) => lead.followStatus === activeFollowStatus.value);
});

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning("请输入管理员账号和密码");
    return;
  }

  isLoggingIn.value = true;

  try {
    const result = await loginAdmin(loginForm);
    setAdminToken(result.token);
    isLoggedIn.value = true;
    ElMessage.success("登录成功");
    await loadLeads();
  } catch (error) {
    ElMessage.error(error.status === 401 ? "账号或密码错误" : error.message || "登录失败");
  } finally {
    isLoggingIn.value = false;
  }
}

function logout() {
  clearAdminToken();
  isLoggedIn.value = false;
  leads.value = [];
}

async function loadLeads() {
  if (!isLoggedIn.value) {
    return;
  }

  isLoadingLeads.value = true;
  try {
    leads.value = await fetchLeads();
  } catch (error) {
    if (error.status === 401) {
      logout();
      ElMessage.warning("登录已失效，请重新登录");
    } else {
      ElMessage.error(error.message || "读取线索失败");
    }
  } finally {
    isLoadingLeads.value = false;
  }
}

async function changeFollowStatus(lead) {
  try {
    const updatedLead = await updateLeadFollowStatus(lead.id, lead.followStatus);
    const index = leads.value.findIndex((item) => item.id === updatedLead.id);
    if (index >= 0) {
      leads.value[index] = updatedLead;
    }
    ElMessage.success("操作成功");
  } catch (error) {
    ElMessage.error(error.message || "更新失败");
    await loadLeads();
  }
}

function levelType(level) {
  if (level === "高意向") return "success";
  if (level === "可跟进") return "warning";
  return "info";
}

onMounted(loadLeads);
</script>

<template>
  <main class="admin-page">
    <section v-if="!isLoggedIn" class="admin-login">
      <div class="login-hero">
        <a class="login-brand" href="/">
          <span class="brand-mark">AI</span>
          <span>
            <strong>悦颜获客助手</strong>
            <small>线索管理后台</small>
          </span>
        </a>
        <div class="login-copy">
          <p class="eyebrow">Admin Console</p>
          <h1>把用户咨询数据沉淀成可跟进的线索表。</h1>
          <p>
            登录后可以查看客户填写的项目、预算、渠道、意向分和跟进状态，适合演示给商家看完整交付链路。
          </p>
        </div>
        <div class="login-stats">
          <div>
            <strong>实时</strong>
            <span>表单入库</span>
          </div>
          <div>
            <strong>分层</strong>
            <span>意向判断</span>
          </div>
          <div>
            <strong>闭环</strong>
            <span>跟进状态</span>
          </div>
        </div>
      </div>

      <el-card class="login-card" shadow="always">
        <div class="login-title">
          <span>欢迎回来</span>
          <strong>管理员登录</strong>
          <p>请输入管理员账号和密码进入数据后台</p>
        </div>

        <el-form
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item label="账号" prop="username">
            <el-input v-model.trim="loginForm.username" autocomplete="username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              autocomplete="current-password"
              placeholder="请输入密码"
              show-password
              type="password"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-button type="primary" size="large" class="login-button" :loading="isLoggingIn" @click="handleLogin">
            登录后台
          </el-button>
        </el-form>
      </el-card>
    </section>

    <section v-else class="admin-shell">
      <header class="admin-header">
        <div>
          <p class="eyebrow">后台管理</p>
          <h1>用户填写数据表</h1>
          <p class="muted">只展示 lead_consultations 表中的核心字段。</p>
        </div>
        <div class="admin-actions">
          <el-button type="primary" :loading="isLoadingLeads" @click="loadLeads">刷新数据</el-button>
          <el-button @click="logout">退出登录</el-button>
          <a class="admin-home-link" href="/">返回前台</a>
        </div>
      </header>

      <div class="admin-metrics">
        <el-card shadow="never">
          <span>线索总数</span>
          <strong>{{ leads.length }}</strong>
        </el-card>
        <el-card shadow="never">
          <span>高意向</span>
          <strong>{{ highIntentCount }}</strong>
        </el-card>
      </div>

      <el-card shadow="never">
        <div class="status-filter">
          <button
            v-for="status in followStatusFilters"
            :key="status"
            class="status-filter-item"
            :class="{ active: activeFollowStatus === status }"
            type="button"
            @click="activeFollowStatus = status"
          >
            <span>{{ status }}</span>
            <strong>{{ statusCounts[status] || 0 }}</strong>
          </button>
        </div>

        <el-table :data="filteredLeads" stripe border v-loading="isLoadingLeads" empty-text="暂无线索数据">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="serviceName" label="咨询项目" min-width="120" />
          <el-table-column prop="budgetRange" label="预算区间" min-width="120" />
          <el-table-column prop="urgency" label="到店时间" min-width="110" />
          <el-table-column prop="customerType" label="客户类型" min-width="110" />
          <el-table-column prop="sourceChannel" label="来源渠道" min-width="110" />
          <el-table-column prop="intentScore" label="意向分" width="100" />
          <el-table-column label="意向等级" width="120">
            <template #default="{ row }">
              <el-tag :type="levelType(row.intentLevel)">{{ row.intentLevel }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="跟进状态" width="150">
            <template #default="{ row }">
              <el-select v-model="row.followStatus" size="small" @change="changeFollowStatus(row)">
                <el-option v-for="status in followStatusOptions" :key="status" :label="status" :value="status" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </section>
  </main>
</template>
