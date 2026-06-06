package com.huoke.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank(message = "账号不能为空")
        String username,

        @NotBlank(message = "密码不能为空")
        String encryptedPassword
) {
}
