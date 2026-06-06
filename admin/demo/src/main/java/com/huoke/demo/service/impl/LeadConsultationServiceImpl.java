package com.huoke.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huoke.demo.dto.LeadCreateRequest;
import com.huoke.demo.entity.LeadConsultation;
import com.huoke.demo.mapper.LeadConsultationMapper;
import com.huoke.demo.service.LeadConsultationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class LeadConsultationServiceImpl
        extends ServiceImpl<LeadConsultationMapper, LeadConsultation>
        implements LeadConsultationService {

    private static final Map<String, Integer> BUDGET_SCORES = Map.of(
            "1000以下", 6,
            "1000-3000", 16,
            "3000-6000", 28,
            "6000以上", 36
    );

    private static final Map<String, Integer> URGENCY_SCORES = Map.of(
            "今天", 30,
            "本周", 24,
            "本月", 14,
            "先了解", 6
    );

    private static final Map<String, Integer> CUSTOMER_SCORES = Map.of(
            "新客", 18,
            "老客", 10,
            "团购客户", 12,
            "朋友介绍", 16
    );

    private static final Map<String, Integer> CHANNEL_SCORES = Map.of(
            "微信", 14,
            "小红书", 13,
            "抖音", 12,
            "美团", 10,
            "其他", 6
    );

    private static final Set<String> FOLLOW_STATUSES = Set.of("待跟进", "已联系", "已成交", "暂不考虑");

    @Override
    public LeadConsultation createLead(LeadCreateRequest request) {
        int score = calculateScore(request);

        LeadConsultation lead = new LeadConsultation();
        lead.setServiceName(request.serviceName().trim());
        lead.setBudgetRange(request.budgetRange());
        lead.setUrgency(request.urgency());
        lead.setCustomerType(request.customerType());
        lead.setSourceChannel(request.sourceChannel());
        lead.setIntentScore(score);
        lead.setIntentLevel(resolveLevel(score));
        lead.setFollowStatus("待跟进");

        save(lead);
        return lead;
    }

    @Override
    public List<LeadConsultation> listLatest() {
        return list(new LambdaQueryWrapper<LeadConsultation>()
                .orderByDesc(LeadConsultation::getId));
    }

    @Override
    public LeadConsultation updateFollowStatus(Long id, String followStatus) {
        String normalizedStatus = StringUtils.hasText(followStatus) ? followStatus.trim() : "待跟进";

        if (!FOLLOW_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(BAD_REQUEST, "不支持的跟进状态");
        }

        int updated = baseMapper.updateFollowStatusById(id, normalizedStatus);
        if (updated == 0) {
            throw new ResponseStatusException(NOT_FOUND, "线索不存在");
        }

        return getById(id);
    }

    private int calculateScore(LeadCreateRequest request) {
        return BUDGET_SCORES.getOrDefault(request.budgetRange(), 8)
                + URGENCY_SCORES.getOrDefault(request.urgency(), 6)
                + CUSTOMER_SCORES.getOrDefault(request.customerType(), 8)
                + CHANNEL_SCORES.getOrDefault(request.sourceChannel(), 6);
    }

    private String resolveLevel(int score) {
        if (score >= 78) {
            return "高意向";
        }
        if (score >= 52) {
            return "可跟进";
        }
        return "待培养";
    }
}
