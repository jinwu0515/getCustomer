package com.huoke.demo.service.impl;

import com.huoke.demo.config.QwenProperties;
import com.huoke.demo.dto.AiFollowUpScriptRequest;
import com.huoke.demo.dto.AiFollowUpScriptResponse;
import com.huoke.demo.entity.AiFollowUpScript;
import com.huoke.demo.entity.LeadConsultation;
import com.huoke.demo.mapper.AiFollowUpScriptMapper;
import com.huoke.demo.mapper.LeadConsultationMapper;
import com.huoke.demo.service.QwenAiService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
public class QwenAiServiceImpl implements QwenAiService {

    private static final String SYSTEM_PROMPT = """
            你是一个本地门店和服务型商家的获客销售顾问。
            你需要先根据客户咨询项目判断店铺类型和行业场景，再生成自然、克制、有成交动作的中文跟进话术。
            输出要能直接复制给销售使用，不要夸大承诺，不要编造价格、效果、资质或服务细节。
            """;

    private final QwenProperties properties;
    private final RestClient restClient;
    private final AiFollowUpScriptMapper aiFollowUpScriptMapper;
    private final LeadConsultationMapper leadConsultationMapper;

    public QwenAiServiceImpl(
            QwenProperties properties,
            RestClient.Builder restClientBuilder,
            AiFollowUpScriptMapper aiFollowUpScriptMapper,
            LeadConsultationMapper leadConsultationMapper
    ) {
        this.properties = properties;
        this.aiFollowUpScriptMapper = aiFollowUpScriptMapper;
        this.leadConsultationMapper = leadConsultationMapper;
        this.restClient = restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Override
    public AiFollowUpScriptResponse generateFollowUpScript(AiFollowUpScriptRequest request) {
        AiFollowUpScript storedScript = aiFollowUpScriptMapper.selectByLeadId(request.leadId());
        if (storedScript != null && StringUtils.hasText(storedScript.getContent())) {
            return new AiFollowUpScriptResponse(storedScript.getContent());
        }

        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Qwen API Key 未配置");
        }

        String trimmedContent = requestQwenContent(request);
        saveGeneratedScript(request, trimmedContent);
        return new AiFollowUpScriptResponse(trimmedContent);
    }

    @Override
    public List<AiFollowUpScript> listStoredFollowUpScripts() {
        return aiFollowUpScriptMapper.selectLatest();
    }

    @Override
    public AiFollowUpScript regenerateFollowUpScript(Long scriptId) {
        AiFollowUpScript storedScript = aiFollowUpScriptMapper.selectById(scriptId);
        if (storedScript == null) {
            throw new ResponseStatusException(NOT_FOUND, "AI 话术不存在");
        }

        LeadConsultation lead = leadConsultationMapper.selectById(storedScript.getLeadId());
        if (lead == null) {
            throw new ResponseStatusException(NOT_FOUND, "线索不存在");
        }

        AiFollowUpScriptRequest request = new AiFollowUpScriptRequest(
                lead.getId(),
                lead.getServiceName(),
                lead.getBudgetRange(),
                lead.getUrgency(),
                lead.getCustomerType(),
                lead.getSourceChannel(),
                lead.getIntentLevel()
        );
        String content = requestQwenContent(request);

        storedScript.setServiceName(lead.getServiceName());
        storedScript.setContent(content);
        storedScript.setModel(properties.getModel());
        storedScript.setUpdatedAt(LocalDateTime.now());
        aiFollowUpScriptMapper.updateById(storedScript);

        return storedScript;
    }

    private String requestQwenContent(AiFollowUpScriptRequest request) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Qwen API Key 未配置");
        }

        QwenChatResponse response = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildChatRequest(request))
                .retrieve()
                .body(QwenChatResponse.class);

        String content = extractContent(response);
        if (!StringUtils.hasText(content)) {
            throw new ResponseStatusException(BAD_GATEWAY, "Qwen 未返回有效内容");
        }

        return content.trim();
    }

    private void saveGeneratedScript(AiFollowUpScriptRequest request, String content) {
        LocalDateTime now = LocalDateTime.now();
        AiFollowUpScript script = new AiFollowUpScript();
        script.setLeadId(request.leadId());
        script.setServiceName(request.serviceName());
        script.setContent(content);
        script.setModel(properties.getModel());
        script.setCreatedAt(now);
        script.setUpdatedAt(now);
        try {
            aiFollowUpScriptMapper.insert(script);
        } catch (DuplicateKeyException error) {
            AiFollowUpScript storedScript = aiFollowUpScriptMapper.selectByLeadId(request.leadId());
            if (storedScript == null || !StringUtils.hasText(storedScript.getContent())) {
                throw error;
            }
        }
    }

    private QwenChatRequest buildChatRequest(AiFollowUpScriptRequest request) {
        return new QwenChatRequest(
                properties.getModel(),
                List.of(
                        new QwenMessage("system", SYSTEM_PROMPT),
                        new QwenMessage("user", buildUserPrompt(request))
                ),
                0.7,
                0.8,
                800
        );
    }

    private String buildUserPrompt(AiFollowUpScriptRequest request) {
        return """
                请根据下面这条咨询线索，先判断客户咨询的店铺/服务类型，再生成一份贴合该行业的销售跟进建议。

                咨询店铺/服务类型：%s
                预算区间：%s
                到店时间：%s
                客户类型：%s
                来源渠道：%s
                意向等级：%s

                请按以下结构输出：
                1. 店铺/服务类型判断
                2. 客户情况判断
                3. 推荐私聊话术
                4. 成交切入点
                5. 下一步跟进动作
                6. 注意事项
                """.formatted(
                request.serviceName(),
                request.budgetRange(),
                request.urgency(),
                request.customerType(),
                request.sourceChannel(),
                request.intentLevel()
        );
    }

    private String extractContent(QwenChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return "";
        }

        QwenChoice choice = response.choices().get(0);
        if (choice == null || choice.message() == null) {
            return "";
        }

        return choice.message().content();
    }

    private record QwenChatRequest(
            String model,
            List<QwenMessage> messages,
            double temperature,
            double top_p,
            int max_tokens
    ) {
    }

    private record QwenMessage(String role, String content) {
    }

    private record QwenChatResponse(List<QwenChoice> choices) {
    }

    private record QwenChoice(QwenMessage message) {
    }
}
