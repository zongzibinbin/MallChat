-- 用户名密码登录改造
ALTER TABLE `user`
    MODIFY COLUMN `open_id` char(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信openid用户标识';

ALTER TABLE `user`
    ADD COLUMN `username` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录账号' AFTER `name`;

ALTER TABLE `user`
    ADD COLUMN `password_hash` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码哈希' AFTER `username`;

ALTER TABLE `user`
    ADD UNIQUE KEY `uniq_username` (`username`) USING BTREE;
