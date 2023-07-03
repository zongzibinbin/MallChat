CREATE TABLE `mc_emojis` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `user_id` bigint(20) NOT NULL COMMENT '用户表ID',
                             `expression_url` varchar(255) NOT NULL COMMENT '表情地址',
                             `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                             `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                             `del_flg` int(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除(0-正常,1-删除)',
                             PRIMARY KEY (`id`),
                             KEY `IDX_MC_EMOJIS_USER_ID` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='用户表情包';