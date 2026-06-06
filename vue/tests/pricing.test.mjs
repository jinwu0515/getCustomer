import assert from "node:assert/strict";
import { buildLeadPlan, scoreLead } from "../src/pricing.js";

const highIntent = scoreLead({
  budget: "6000以上",
  urgency: "今天",
  customerType: "新客",
  channel: "微信",
});

assert.equal(highIntent.score, 98);
assert.equal(highIntent.level, "高意向");

const plan = buildLeadPlan({
  service: "皮肤管理",
  budget: "3000-6000",
  urgency: "本周",
  customerType: "新客",
  channel: "小红书",
});

assert.equal(plan.packageName, "标准转化包");
assert.equal(plan.price, "3980");
assert.match(plan.quote, /标准转化包建议报价 3980 元/);
assert.match(plan.reply, /皮肤管理/);
assert.match(plan.nextStep, /20 分钟需求确认/);
