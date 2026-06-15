package com.huoke.demo.service;

import com.huoke.demo.dto.AiFollowUpScriptRequest;
import com.huoke.demo.dto.AiFollowUpScriptResponse;
import com.huoke.demo.entity.AiFollowUpScript;

import java.util.List;

public interface QwenAiService {

    AiFollowUpScriptResponse generateFollowUpScript(AiFollowUpScriptRequest request);

    List<AiFollowUpScript> listStoredFollowUpScripts();

    AiFollowUpScript regenerateFollowUpScript(Long scriptId);
}
