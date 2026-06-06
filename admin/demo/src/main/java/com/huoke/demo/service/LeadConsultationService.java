package com.huoke.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huoke.demo.dto.LeadCreateRequest;
import com.huoke.demo.entity.LeadConsultation;

import java.util.List;

public interface LeadConsultationService extends IService<LeadConsultation> {

    LeadConsultation createLead(LeadCreateRequest request);

    List<LeadConsultation> listLatest();

    LeadConsultation updateFollowStatus(Long id, String followStatus);
}
