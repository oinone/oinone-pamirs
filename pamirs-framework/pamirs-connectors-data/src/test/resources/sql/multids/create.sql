CREATE TABLE IF NOT EXISTS `master`.`test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '#id#ID字段，唯一自增索引',
  `module` varchar(128) unique COLLATE utf8mb4_bin NOT NULL COMMENT '#module#',
  `version` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '#version#',
  `relation` text COLLATE utf8mb4_bin NOT NULL COMMENT '#relation#',
  `opt_version` bigint(20) DEFAULT '0' COMMENT '#opt#',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '#createDate#',
  `write_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '#writeDate#',
  `create_uid` bigint(20) DEFAULT NULL COMMENT '#createUid#',
  `write_uid` bigint(20) DEFAULT NULL COMMENT '#writeUid#',
  `is_deleted` bigint(20) DEFAULT '0' COMMENT '#is_deleted#逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `base_test_module` (`module`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='测试表';