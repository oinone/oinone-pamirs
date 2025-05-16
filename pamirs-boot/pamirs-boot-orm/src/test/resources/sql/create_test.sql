CREATE DATABASE IF NOT EXISTS `testorm`;
use `testorm`;

CREATE TABLE IF NOT EXISTS `test_code_model`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `name` varchar(128) COMMENT '',
 `code` varchar(128) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '应用分类';
CREATE UNIQUE INDEX `test_code_model_code` ON `test_code_model`(`code`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `test_many_to_many_model`(
 `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `name` VARCHAR(128) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 `create_uid` BIGINT(20) COMMENT '创建人',
 `write_uid` BIGINT(20) COMMENT '更新人',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '应用分类';

CREATE TABLE IF NOT EXISTS `test_many_to_many_model_rel`(
 `test_many_to_many_model_id` BIGINT(20) NOT NULL COMMENT 'ID字段，唯一自增索引',
 `module_category_id` BIGINT(20) NOT NULL COMMENT 'ID字段，唯一自增索引',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 `create_uid` BIGINT(20) COMMENT '创建人',
 `write_uid` BIGINT(20) COMMENT '更新人',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`test_many_to_many_model_id`, `module_category_id`, `is_deleted`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '应用分类关系表';

CREATE TABLE IF NOT EXISTS `test_inherited_reentry_model`(
 `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `name` VARCHAR(128) UNIQUE COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 `create_uid` BIGINT(20) COMMENT '创建人',
 `write_uid` BIGINT(20) COMMENT '更新人',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '重入测试模型';

