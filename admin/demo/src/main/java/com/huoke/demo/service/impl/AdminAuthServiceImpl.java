package com.huoke.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huoke.demo.dto.AdminLoginRequest;
import com.huoke.demo.dto.AdminLoginResponse;
import com.huoke.demo.entity.AdminUser;
import com.huoke.demo.mapper.AdminUserMapper;
import com.huoke.demo.service.AdminAuthService;
import com.huoke.demo.service.LoginPasswordCryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final LoginPasswordCryptoService loginPasswordCryptoService;
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, request.username().trim())
                .last("limit 1"));
        String rawPassword = loginPasswordCryptoService.decryptPassword(request.encryptedPassword());

        if (adminUser == null
                || adminUser.getStatus() == null
                || adminUser.getStatus() != 1
                || adminUser.getPassword() == null
                || !passwordEncoder.matches(rawPassword, adminUser.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "账号或密码错误");
        }

        String token = UUID.randomUUID().toString();
        sessions.put(token, adminUser.getId());

        return new AdminLoginResponse(token, adminUser.getUsername(), adminUser.getNickname());
    }

    @Override
    public boolean isValidToken(String token) {
        return token != null && sessions.containsKey(token);
    }
}
