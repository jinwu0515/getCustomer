ALTER TABLE admin_users
  MODIFY password VARCHAR(255) NOT NULL COMMENT '密码哈希';
