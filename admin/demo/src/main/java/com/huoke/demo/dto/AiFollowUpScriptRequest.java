package com.huoke.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiFollowUpScriptRequest(
        @NotNull(message = "线索 ID 不能为空")
        Long leadId,

        @NotBlank(message = "咨询店铺/服务类型不能为空")
        String serviceName,

        @NotBlank(message = "预算区间不能为空")
        String budgetRange,

        @NotBlank(message = "到店时间不能为空")
        String urgency,

        @NotBlank(message = "客户类型不能为空")
        String customerType,

        @NotBlank(message = "来源渠道不能为空")
        String sourceChannel,

        @NotBlank(message = "意向等级不能为空")
        String intentLevel
) {
}
