###接口埋点日志表
DROP TABLE IF EXISTS `web_log`;
CREATE TABLE `web_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `trace_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链路追踪id，对应MDC中的tid',
  `uid` bigint(20) DEFAULT NULL COMMENT '用户id，未登录为空',
  `ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户端ip，兼容ipv6',
  `http_method` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求方法',
  `uri` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求路径',
  `class_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Controller类名#方法名',
  `request_param` json DEFAULT NULL COMMENT '请求参数快照，不包含ServletRequest/ServletResponse',
  `response_result` json DEFAULT NULL COMMENT '响应结果快照',
  `success` tinyint(1) DEFAULT NULL COMMENT '是否成功 1成功 0失败，非ApiResult为空',
  `err_code` int(11) DEFAULT NULL COMMENT '错误码，ApiResult失败时记录',
  `err_msg` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息，ApiResult失败时记录',
  `exception_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '异常类名',
  `exception_msg` text COLLATE utf8mb4_unicode_ci COMMENT '异常信息',
  `cost_ms` bigint(20) NOT NULL DEFAULT '0' COMMENT '接口耗时，单位毫秒',
  `user_agent` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'User-Agent',
  `referer` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Referer',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_trace_id` (`trace_id`) USING BTREE,
  KEY `idx_uid_create_time` (`uid`, `create_time`) USING BTREE,
  KEY `idx_uri_create_time` (`uri`(128), `create_time`) USING BTREE,
  KEY `idx_success_create_time` (`success`, `create_time`) USING BTREE,
  KEY `idx_create_time_cost` (`create_time`, `cost_ms`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='接口埋点日志表' ROW_FORMAT=DYNAMIC;
