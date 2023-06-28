DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word` (
  `word` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '敏感词'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏感词库';
INSERT INTO `sensitive_word` (`word`) VALUES ('TMD');
INSERT INTO `sensitive_word` (`word`) VALUES ('tmd');