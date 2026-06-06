package com.huoke.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record LeadCreateRequest(
        @NotBlank(message = "客户咨询项目不能为空")
        String serviceName,

        @NotBlank(message = "预算区间不能为空")
        String budgetRange,

        @NotBlank(message = "到店时间不能为空")
        String urgency,

        @NotBlank(message = "客户类型不能为空")
        String customerType,

        @NotBlank(message = "来源渠道不能为空")
        String sourceChannel
) {
}
