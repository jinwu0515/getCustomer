package com.huoke.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record FollowStatusUpdateRequest(
        @NotBlank(message = "跟进状态不能为空")
        String followStatus
) {
}
