const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const ADMIN_TOKEN_KEY = "lead_admin_token";

export function getAdminToken() {
  return localStorage.getItem(ADMIN_TOKEN_KEY) || "";
}

export function setAdminToken(token) {
  localStorage.setItem(ADMIN_TOKEN_KEY, token);
}

export function clearAdminToken() {
  localStorage.removeItem(ADMIN_TOKEN_KEY);
}

async function request(path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...headers,
    },
  });

  if (!response.ok) {
    const text = await response.text();
    const data = parseJsonText(text);
    const message = data?.reason || data?.message || data?.error || text;

    const error = new Error(message || `请求失败：${response.status}`);
    error.status = response.status;
    throw error;
  }

  const text = await response.text();
  return parseJsonText(text);
}

function parseJsonText(text) {
  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

function withAdminAuth(options = {}) {
  const token = getAdminToken();
  return {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: `Bearer ${token}`,
    },
  };
}

export function loginAdmin(payload) {
  return encryptLoginPassword(payload.password).then((encryptedPassword) => request("/api/admin/login", {
    method: "POST",
    body: JSON.stringify({
      username: payload.username,
      encryptedPassword,
    }),
  }));
}

async function encryptLoginPassword(password) {
  if (!window.crypto?.subtle) {
    throw new Error("当前浏览器不支持登录加密，请使用现代浏览器或启用 HTTPS");
  }

  const data = await request("/api/admin/login-public-key");
  const publicKey = await importRsaPublicKey(data.publicKey);
  const encrypted = await window.crypto.subtle.encrypt(
    {
      name: "RSA-OAEP",
    },
    publicKey,
    new TextEncoder().encode(password),
  );

  return arrayBufferToBase64(encrypted);
}

async function importRsaPublicKey(pem) {
  const keyData = pemToArrayBuffer(pem);
  return window.crypto.subtle.importKey(
    "spki",
    keyData,
    {
      name: "RSA-OAEP",
      hash: "SHA-256",
    },
    false,
    ["encrypt"],
  );
}

function pemToArrayBuffer(pem) {
  const base64 = pem
    .replace("-----BEGIN PUBLIC KEY-----", "")
    .replace("-----END PUBLIC KEY-----", "")
    .replace(/\s/g, "");
  const binary = window.atob(base64);
  const bytes = new Uint8Array(binary.length);

  for (let index = 0; index < binary.length; index += 1) {
    bytes[index] = binary.charCodeAt(index);
  }

  return bytes.buffer;
}

function arrayBufferToBase64(buffer) {
  const bytes = new Uint8Array(buffer);
  let binary = "";

  for (let index = 0; index < bytes.byteLength; index += 1) {
    binary += String.fromCharCode(bytes[index]);
  }

  return window.btoa(binary);
}

export function createLead(payload) {
  return request("/api/leads", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function fetchLeads() {
  return request("/api/leads", withAdminAuth());
}

export function fetchAiFollowUpScripts() {
  return request("/api/ai/follow-up-scripts", withAdminAuth());
}

export function regenerateAiFollowUpScript(id) {
  return request(`/api/ai/follow-up-scripts/${id}/regenerate`, withAdminAuth({
    method: "POST",
  }));
}

export function updateLeadFollowStatus(id, followStatus) {
  return request(`/api/leads/${id}/follow-status`, withAdminAuth({
    method: "PATCH",
    body: JSON.stringify({ followStatus }),
  }));
}

export function generateFollowUpScript(lead) {
  return request("/api/ai/follow-up-script", withAdminAuth({
    method: "POST",
    body: JSON.stringify({
      leadId: lead.id,
      serviceName: lead.serviceName,
      budgetRange: lead.budgetRange,
      urgency: lead.urgency,
      customerType: lead.customerType,
      sourceChannel: lead.sourceChannel,
      intentLevel: lead.intentLevel,
    }),
  }));
}
