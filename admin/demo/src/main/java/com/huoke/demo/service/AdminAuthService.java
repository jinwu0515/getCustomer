package com.huoke.demo.service;

import com.huoke.demo.dto.AdminLoginRequest;
import com.huoke.demo.dto.AdminLoginResponse;

public interface AdminAuthService {

    AdminLoginResponse login(AdminLoginRequest request);

    boolean isValidToken(String token);
}
