package com.huoke.demo.dto;

public record AdminLoginResponse(
        String token,
        String username,
        String nickname
) {
}
