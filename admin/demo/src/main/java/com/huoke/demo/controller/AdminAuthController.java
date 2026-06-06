package com.huoke.demo.controller;

import com.huoke.demo.dto.AdminLoginRequest;
import com.huoke.demo.dto.AdminLoginPublicKeyResponse;
import com.huoke.demo.dto.AdminLoginResponse;
import com.huoke.demo.service.AdminAuthService;
import com.huoke.demo.service.LoginPasswordCryptoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final LoginPasswordCryptoService loginPasswordCryptoService;

    @GetMapping("/login-public-key")
    public AdminLoginPublicKeyResponse getLoginPublicKey() {
        return new AdminLoginPublicKeyResponse(loginPasswordCryptoService.getPublicKeyPem());
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) {
        return adminAuthService.login(request);
    }
}
