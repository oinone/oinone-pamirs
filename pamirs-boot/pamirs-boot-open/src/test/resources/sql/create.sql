CREATE DATABASE IF NOT EXISTS `testorm`;
use `testorm`;

CREATE TABLE IF NOT EXISTS `base_module`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `name` varchar(128) COMMENT '',
 `module` varchar(128) COMMENT '',
 `ds_key` varchar(128) COMMENT '',
 `summary` varchar(128) COMMENT '',
 `description` text COMMENT '',
 `state` varchar(128) COMMENT '',
 `source` varchar(128) COMMENT '',
 `boot` tinyint(1) COMMENT '',
 `application` tinyint(1) COMMENT '',
 `latest_version` varchar(128) COMMENT '',
 `platform_version` varchar(128) COMMENT '',
 `published_version` bigint(20) COMMENT '',
 `publish_count` bigint(20) COMMENT '',
 `category` varchar(128) COMMENT '分类编码',
 `priority` bigint(20) COMMENT '决定了模块在展示和首页默认跳转的顺序，默认100，排序时默认按priority和name排序',
 `website` varchar(128) COMMENT '',
 `author` varchar(128) COMMENT '',
 `demo` tinyint(1) COMMENT '',
 `web` tinyint(1) COMMENT '',
 `license` varchar(128) COMMENT '',
 `to_buy` tinyint(1) COMMENT '',
 `maintainer` varchar(128) COMMENT '',
 `contributors` text COMMENT '',
 `url` varchar(128) COMMENT '',
 `self_built` tinyint(1) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '模块';
CREATE INDEX `module_state` ON `base_module`(`state`);
CREATE INDEX `module_source` ON `base_module`(`source`);
CREATE UNIQUE INDEX `module_module` ON `base_module`(`module`,`is_deleted`);
CREATE UNIQUE INDEX `module_name` ON `base_module`(`name`,`is_deleted`);
