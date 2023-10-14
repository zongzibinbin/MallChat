###本地消息表
DROP TABLE IF EXISTS `secure_invoke_record`;
CREATE TABLE `secure_invoke_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `secure_invoke_json` json NOT NULL COMMENT '请求快照参数json',
  `status` tinyint(8) NOT NULL COMMENT '状态 1待执行 2已失败',
  `next_retry_time` datetime(3) NOT NULL COMMENT '下一次重试的时间',
  `retry_times` int(11) NOT NULL COMMENT '已经重试的次数',
  `max_retry_times` int(11) NOT NULL COMMENT '最大重试次数',
  `fail_reason` text COMMENT '执行失败的堆栈',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_next_retry_time` (`next_retry_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地消息表';