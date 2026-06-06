const budgetScores = {
  "1000以下": 6,
  "1000-3000": 16,
  "3000-6000": 28,
  "6000以上": 36,
};

const urgencyScores = {
  今天: 30,
  本周: 24,
  本月: 14,
  先了解: 6,
};

const customerScores = {
  新客: 18,
  老客: 10,
  团购客户: 12,
  朋友介绍: 16,
};

const channelScores = {
  微信: 14,
  小红书: 13,
  抖音: 12,
  美团: 10,
  其他: 6,
};

const quoteByBudget = {
  "1000以下": {
    packageName: "轻量体验包",
    price: "1980",
    depositRate: "30%",
    focus: "先完成门店介绍、留资表单和基础咨询回复。",
  },
  "1000-3000": {
    packageName: "基础获客包",
    price: "2980",
    depositRate: "30%",
    focus: "适合快速上线获客页，并把咨询问题整理成自动回复。",
  },
  "3000-6000": {
    packageName: "标准转化包",
    price: "3980",
    depositRate: "40%",
    focus: "增加客户意向评分、套餐推荐和预约跟进话术。",
  },
  "6000以上": {
    packageName: "增长交付包",
    price: "6980",
    depositRate: "50%",
    focus: "加入数据表、自动跟进、成交复盘和维护方案。",
  },
};

export const formOptions = {
  budgets: Object.keys(budgetScores),
  urgencies: Object.keys(urgencyScores),
  customerTypes: Object.keys(customerScores),
  channels: Object.keys(channelScores),
};

export function scoreLead({ budget, urgency, customerType, channel }) {
  const score =
    (budgetScores[budget] ?? 8) +
    (urgencyScores[urgency] ?? 6) +
    (customerScores[customerType] ?? 8) +
    (channelScores[channel] ?? 6);

  let level = "待培养";
  if (score >= 78) level = "高意向";
  else if (score >= 52) level = "可跟进";

  return { score, level };
}

export function buildLeadPlan(input) {
  const budgetPlan = quoteByBudget[input.budget] ?? quoteByBudget["1000-3000"];
  const scored = scoreLead(input);
  const service = input.service || "门店服务";
  const urgency = input.urgency || "本周";

  return {
    ...budgetPlan,
    ...scored,
    quote: `${budgetPlan.packageName}建议报价 ${budgetPlan.price} 元，先收 ${budgetPlan.depositRate} 定金。${budgetPlan.focus}`,
    reply: `您好，您咨询的${service}可以先按“咨询-留资-预约”三步做转化。我会先帮您把客户常问问题、套餐卖点和预约入口整理成一个页面，方便客户扫码后直接留下需求。`,
    nextStep:
      urgency === "今天"
        ? "今天先发门店资料和套餐价格，晚上前给出页面初稿，确认后收定金开做。"
        : `建议先预约一次 20 分钟需求确认，把${service}的套餐、客户问题和成交流程整理清楚。`,
    maintenance:
      "交付后可选 499 元/月维护包：每月更新文案、表单字段、客户问题和一次数据复盘。",
  };
}
