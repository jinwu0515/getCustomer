package com.huoke.demo.service.impl;

import com.huoke.demo.config.QwenProperties;
import com.huoke.demo.dto.AiFollowUpScriptRequest;
import com.huoke.demo.dto.AiFollowUpScriptResponse;
import com.huoke.demo.entity.AiFollowUpScript;
import com.huoke.demo.entity.LeadConsultation;
import com.huoke.demo.mapper.AiFollowUpScriptMapper;
import com.huoke.demo.mapper.LeadConsultationMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(QwenAiServiceImpl.class)
@EnableConfigurationProperties(QwenProperties.class)
@TestPropertySource(properties = {
        "qwen.api-key=test-qwen-key",
        "qwen.base-url=http://qwen.test/compatible-mode/v1",
        "qwen.model=qwen-plus"
})
class QwenAiServiceImplTest {

    @Autowired
    private QwenAiServiceImpl qwenAiService;

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    private AiFollowUpScriptMapper aiFollowUpScriptMapper;

    @MockBean
    private LeadConsultationMapper leadConsultationMapper;

    @Test
    void generateFollowUpScriptSendsLeadContextToQwenAndReturnsContent() {
        when(aiFollowUpScriptMapper.selectByLeadId(7L)).thenReturn(null);

        server.expect(requestTo("http://qwen.test/compatible-mode/v1/chat/completions"))
                .andExpect(method(POST))
                .andExpect(header(AUTHORIZATION, "Bearer test-qwen-key"))
                .andExpect(content().string(containsString("\"model\":\"qwen-plus\"")))
                .andExpect(content().string(containsString("健身私教")))
                .andExpect(content().string(not(containsString("美业门店"))))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "客户目标明确，建议先用体验课切入。"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        AiFollowUpScriptResponse response = qwenAiService.generateFollowUpScript(new AiFollowUpScriptRequest(
                7L,
                "健身私教",
                "3000-6000",
                "本周",
                "新客",
                "抖音",
                "高意向"
        ));

        assertEquals("客户目标明确，建议先用体验课切入。", response.content());
        verify(aiFollowUpScriptMapper).insert(any(AiFollowUpScript.class));
        server.verify();
    }

    @Test
    void generateFollowUpScriptReturnsStoredScriptWithoutCallingQwen() {
        AiFollowUpScript storedScript = new AiFollowUpScript();
        storedScript.setId(3L);
        storedScript.setLeadId(7L);
        storedScript.setContent("已保存的话术");
        when(aiFollowUpScriptMapper.selectByLeadId(7L)).thenReturn(storedScript);

        AiFollowUpScriptResponse response = qwenAiService.generateFollowUpScript(new AiFollowUpScriptRequest(
                7L,
                "健身私教",
                "3000-6000",
                "本周",
                "新客",
                "抖音",
                "高意向"
        ));

        assertEquals("已保存的话术", response.content());
        verify(aiFollowUpScriptMapper, never()).insert(any(AiFollowUpScript.class));
        server.verify();
    }

    @Test
    void listStoredFollowUpScriptsReturnsLatestScriptsFromDatabase() {
        AiFollowUpScript script = new AiFollowUpScript();
        script.setId(9L);
        script.setLeadId(7L);
        script.setServiceName("健身私教");
        script.setContent("已保存的话术");
        script.setModel("qwen-plus");
        script.setUpdatedAt(LocalDateTime.of(2026, 6, 15, 20, 30));
        when(aiFollowUpScriptMapper.selectLatest()).thenReturn(List.of(script));

        List<AiFollowUpScript> scripts = qwenAiService.listStoredFollowUpScripts();

        assertEquals(1, scripts.size());
        assertEquals("已保存的话术", scripts.get(0).getContent());
    }

    @Test
    void regenerateFollowUpScriptCallsQwenAndUpdatesExistingScript() {
        AiFollowUpScript storedScript = new AiFollowUpScript();
        storedScript.setId(9L);
        storedScript.setLeadId(7L);
        storedScript.setServiceName("Yoga Studio");
        storedScript.setContent("old script");
        storedScript.setModel("qwen-plus");
        storedScript.setUpdatedAt(LocalDateTime.of(2026, 6, 15, 20, 30));
        when(aiFollowUpScriptMapper.selectById(9L)).thenReturn(storedScript);

        LeadConsultation lead = new LeadConsultation();
        lead.setId(7L);
        lead.setServiceName("Yoga Studio");
        lead.setBudgetRange("3000-6000");
        lead.setUrgency("This week");
        lead.setCustomerType("New customer");
        lead.setSourceChannel("Rednote");
        lead.setIntentLevel("High intent");
        when(leadConsultationMapper.selectById(7L)).thenReturn(lead);

        server.expect(requestTo("http://qwen.test/compatible-mode/v1/chat/completions"))
                .andExpect(method(POST))
                .andExpect(header(AUTHORIZATION, "Bearer test-qwen-key"))
                .andExpect(content().string(containsString("Yoga Studio")))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "new regenerated script"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        AiFollowUpScript response = qwenAiService.regenerateFollowUpScript(9L);

        ArgumentCaptor<AiFollowUpScript> scriptCaptor = ArgumentCaptor.forClass(AiFollowUpScript.class);
        verify(aiFollowUpScriptMapper).updateById(scriptCaptor.capture());
        assertEquals(9L, scriptCaptor.getValue().getId());
        assertEquals("new regenerated script", scriptCaptor.getValue().getContent());
        assertEquals("new regenerated script", response.getContent());
        server.verify();
    }
}
