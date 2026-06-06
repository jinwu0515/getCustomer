package com.huoke.demo.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huoke.demo.entity.AdminUser;
import com.huoke.demo.mapper.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        AdminUser adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, "admin")
                .last("limit 1"));

        if (adminUser == null) {
            adminUser = new AdminUser();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setNickname("默认管理员");
            adminUser.setStatus(1);
            adminUserMapper.insert(adminUser);
            return;
        }

        if (needsPasswordHash(adminUser.getPassword())) {
            String rawPassword = adminUser.getPassword();
            if (rawPassword == null || rawPassword.isBlank()) {
                rawPassword = "admin";
            }
            adminUser.setPassword(passwordEncoder.encode(rawPassword));
            adminUserMapper.updateById(adminUser);
        }
    }

    private boolean needsPasswordHash(String storedPassword) {
        return storedPassword == null
                || !(storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$"));
    }
}
