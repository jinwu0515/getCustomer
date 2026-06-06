package com.huoke.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.huoke.demo.dto.AdminLoginRequest;
import com.huoke.demo.dto.AdminLoginResponse;
import com.huoke.demo.entity.AdminUser;
import com.huoke.demo.mapper.AdminUserMapper;
import com.huoke.demo.service.LoginPasswordCryptoService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminAuthServiceImplTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AdminUserMapper adminUserMapper = mock(AdminUserMapper.class);
    private final LoginPasswordCryptoService loginPasswordCryptoService = mock(LoginPasswordCryptoService.class);
    private final AdminAuthServiceImpl adminAuthService = new AdminAuthServiceImpl(
            adminUserMapper,
            passwordEncoder,
            loginPasswordCryptoService
    );

    @Test
    void loginAcceptsRawPasswordWhenStoredPasswordIsBCryptHash() {
        AdminUser adminUser = activeAdmin(passwordEncoder.encode("admin"));
        when(adminUserMapper.selectOne(anyAdminWrapper())).thenReturn(adminUser);
        when(loginPasswordCryptoService.decryptPassword("encrypted-admin")).thenReturn("admin");

        AdminLoginResponse response = adminAuthService.login(new AdminLoginRequest("admin", "encrypted-admin"));

        assertEquals("admin", response.username());
        assertTrue(adminAuthService.isValidToken(response.token()));
    }

    @Test
    void loginRejectsRawPasswordWhenStoredPasswordIsPlainText() {
        AdminUser adminUser = activeAdmin("admin");
        when(adminUserMapper.selectOne(anyAdminWrapper())).thenReturn(adminUser);
        when(loginPasswordCryptoService.decryptPassword("encrypted-admin")).thenReturn("admin");

        assertThrows(ResponseStatusException.class,
                () -> adminAuthService.login(new AdminLoginRequest("admin", "encrypted-admin")));
    }

    private AdminUser activeAdmin(String storedPassword) {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword(storedPassword);
        adminUser.setNickname("默认管理员");
        adminUser.setStatus(1);
        return adminUser;
    }

    private Wrapper<AdminUser> anyAdminWrapper() {
        return any();
    }
}
