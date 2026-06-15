package com.huoke.demo.controller;

import com.huoke.demo.dto.AiFollowUpScriptRequest;
import com.huoke.demo.dto.AiFollowUpScriptResponse;
import com.huoke.demo.entity.AiFollowUpScript;
import com.huoke.demo.service.QwenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final QwenAiService qwenAiService;

    @PostMapping("/follow-up-script")
    public AiFollowUpScriptResponse generateFollowUpScript(@Valid @RequestBody AiFollowUpScriptRequest request) {
        return qwenAiService.generateFollowUpScript(request);
    }

    @GetMapping("/follow-up-scripts")
    public List<AiFollowUpScript> listFollowUpScripts() {
        return qwenAiService.listStoredFollowUpScripts();
    }

    @PostMapping("/follow-up-scripts/{id}/regenerate")
    public AiFollowUpScript regenerateFollowUpScript(@PathVariable Long id) {
        return qwenAiService.regenerateFollowUpScript(id);
    }
}
