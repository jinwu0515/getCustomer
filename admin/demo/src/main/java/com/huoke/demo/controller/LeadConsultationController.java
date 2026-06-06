package com.huoke.demo.controller;

import com.huoke.demo.dto.FollowStatusUpdateRequest;
import com.huoke.demo.dto.LeadCreateRequest;
import com.huoke.demo.entity.LeadConsultation;
import com.huoke.demo.service.LeadConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leads")
public class LeadConsultationController {

    private final LeadConsultationService leadConsultationService;

    @PostMapping
    public LeadConsultation create(@Valid @RequestBody LeadCreateRequest request) {
        return leadConsultationService.createLead(request);
    }

    @GetMapping
    public List<LeadConsultation> list() {
        return leadConsultationService.listLatest();
    }

    @PatchMapping("/{id}/follow-status")
    public LeadConsultation updateFollowStatus(
            @PathVariable Long id,
            @Valid @RequestBody FollowStatusUpdateRequest request
    ) {
        return leadConsultationService.updateFollowStatus(id, request.followStatus());
    }
}
