CREATE DATABASE IF NOT EXISTS `testorm`;
use `testorm`;

CREATE TABLE IF NOT EXISTS `sequence_config`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `code` varchar(128) COMMENT '',
 `prefix` varchar(128) COMMENT '',
 `suffix` varchar(128) COMMENT '',
 `separator` varchar(128) COMMENT '',
 `size` int(11) COMMENT '',
 `format` varchar(128) COMMENT '精确到日期，不包含时间',
 `step` varchar(128) COMMENT '',
 `initial` varchar(128) COMMENT '',
 `sequence` varchar(128) COMMENT '序列生成器',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '序列生成配置';
CREATE UNIQUE INDEX `sequence_config_code` ON `sequence_config`(`code`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `module_dependency`(
 `module` varchar(128) COMMENT '主模块',
 `dependency_module` varchar(128) COMMENT '依赖模块',
 `remark` varchar(128) COMMENT '描述',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`module`,`dependency_module`,`is_deleted`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '模块依赖';

CREATE TABLE IF NOT EXISTS `model_inherited`(
 `model` varchar(128) COMMENT '',
 `super_model` varchar(128) COMMENT '',
 `type` varchar(512) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`model`,`super_model`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '模型继承';

CREATE TABLE IF NOT EXISTS `check_expression`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `expression` varchar(128) COMMENT '表达式',
 `rule` varchar(128) COMMENT '校验规则',
 `display_name` varchar(128) COMMENT '',
 `description` varchar(128) COMMENT '描述',
 `message` varchar(128) COMMENT '返回信息',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '校验表达式';

CREATE TABLE IF NOT EXISTS `event`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `description` varchar(128) COMMENT '',
 `active` tinyint(1) COMMENT '',
 `source_function_id` bigint(20) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '';

CREATE TABLE IF NOT EXISTS `model_index`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `unique` tinyint(1) COMMENT '',
 `model` varchar(128) COMMENT '',
 `fields` varchar(128) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '模型索引';
CREATE UNIQUE INDEX `model_index_model_fields` ON `model_index`(`model`,`fields`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `ext_point`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `name` varchar(128) COMMENT '技术名称',
 `namespace` varchar(128) COMMENT '所扩展函数的命名空间',
 `description` varchar(128) COMMENT '描述',
 `active` tinyint(1) COMMENT '',
 `clazz` varchar(128) COMMENT '',
 `method` varchar(128) COMMENT '',
 `argument_list` varchar(1024) COMMENT '',
 `return_type` varchar(512) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '扩展点';
CREATE UNIQUE INDEX `ext_point_namespace_name` ON `ext_point`(`namespace`,`name`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `expression`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `expression` varchar(1024) COMMENT '表达式',
 `description` varchar(128) COMMENT '描述',
 `message` varchar(128) COMMENT '返回信息',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '表达式';

CREATE TABLE IF NOT EXISTS `meta_relation`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `module` varchar(128) COMMENT '',
 `version` varchar(128) COMMENT '',
 `relation` text COMMENT '',
 `opt_version` bigint(20) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '元数据关系';
CREATE UNIQUE INDEX `meta_relation_module` ON `meta_relation`(`module`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `test_function_model`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `field` int(11) COMMENT '',
 `bool` tinyint(1) COMMENT '',
 `type` varchar(128) COMMENT '',
 `ds` varchar(128) COMMENT '',
 `date` datetime COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '测试模型';

CREATE TABLE IF NOT EXISTS `hook`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `module` varchar(128) COMMENT '',
 `model` varchar(128) COMMENT '',
 `fun` varchar(128) COMMENT '',
 `function_types` varchar(512) COMMENT '',
 `hook_type` varchar(128) COMMENT '',
 `priority` int(11) COMMENT '',
 `execute_namespace` varchar(128) COMMENT '',
 `execute_fun` varchar(128) COMMENT '',
 `description` varchar(128) COMMENT '',
 `active` tinyint(1) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '';
CREATE UNIQUE INDEX `hook_execute_namespace_execute_fun` ON `hook`(`execute_namespace`,`execute_fun`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `data_dictionary_item`(
 `display_name` varchar(128) COMMENT '',
 `name` varchar(128) COMMENT '',
 `value` varchar(128) COMMENT '',
 `help` varchar(128) COMMENT '',
 `color` varchar(128) COMMENT '',
 `icon` varchar(128) COMMENT '',
 `extend1` varchar(128) COMMENT '',
 `extend2` varchar(128) COMMENT '',
 `dictionary` varchar(128) COMMENT '',
 `state` tinyint(1) COMMENT '',
 `source` varchar(512) COMMENT 'BASE是系统创建, MANUAL是人工创建',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`dictionary`,`name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '数据字典项';

CREATE TABLE IF NOT EXISTS `module_exclusion`(
 `module` varchar(128) COMMENT '主模块编码',
 `exclude_module` varchar(128) COMMENT '互斥模块编码',
 `remark` varchar(128) COMMENT '描述',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`module`,`exclude_module`,`is_deleted`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '模块互斥';

CREATE TABLE IF NOT EXISTS `data_dictionary`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `module` varchar(128) COMMENT '应用模块',
 `display_name` varchar(128) COMMENT '',
 `dictionary` varchar(128) COMMENT '',
 `name` varchar(128) COMMENT '',
 `lname` varchar(128) COMMENT '',
 `summary` varchar(128) COMMENT '',
 `value_type` varchar(512) COMMENT '',
 `options` text COMMENT '',
 `source` varchar(512) COMMENT 'BASE是系统创建, MANUAL是人工创建',
 `bit` tinyint(1) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '数据字典';
CREATE UNIQUE INDEX `data_dictionary_dictionary` ON `data_dictionary`(`dictionary`,`is_deleted`);

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

CREATE TABLE IF NOT EXISTS `rsql_expression`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `expression` varchar(4096) COMMENT '表达式',
 `display_name` varchar(128) COMMENT '',
 `description` varchar(128) COMMENT '描述',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '校验表达式';

CREATE TABLE IF NOT EXISTS `ext_point_implementation`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `name` varchar(128) COMMENT '技术名称',
 `namespace` varchar(128) COMMENT '扩展点命名空间',
 `execute_namespace` varchar(128) COMMENT '',
 `execute_fun` varchar(128) COMMENT '',
 `description` varchar(128) COMMENT '描述',
 `priority` int(11) COMMENT '',
 `active` tinyint(1) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '扩展点实现';
CREATE INDEX `ext_point_implementation_namespace_name` ON `ext_point_implementation`(`namespace`,`name`);
CREATE UNIQUE INDEX `ext_point_implementation_namespace_name_execute_namespace_execut` ON `ext_point_implementation`(`namespace`,`name`,`execute_namespace`,`execute_fun`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `function`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `namespace` varchar(128) COMMENT '',
 `fun` varchar(128) COMMENT '',
 `name` varchar(128) COMMENT '',
 `language` varchar(512) COMMENT '代码语言',
 `type` varchar(512) COMMENT '函数类型',
 `data_manager` tinyint(1) COMMENT '',
 `bean_name` varchar(128) COMMENT '',
 `imports` text COMMENT '上下文引用',
 `context` text COMMENT '上下文，变量',
 `codes` text COMMENT '代码内容',
 `source` varchar(512) COMMENT '来源',
 `open_level` varchar(512) COMMENT '开放级别',
 `forbidden_level` varchar(512) COMMENT '禁止级别',
 `description` text COMMENT '',
 `category` varchar(512) COMMENT '分类',
 `scene` varchar(512) COMMENT '可用场景',
 `is_builtin` tinyint(1) COMMENT '',
 `is_inherited` tinyint(1) COMMENT '',
 `group` varchar(128) COMMENT '',
 `version` varchar(128) COMMENT '',
 `timeout` int(11) COMMENT '',
 `is_long_polling` tinyint(1) COMMENT '是否支持long polling',
 `long_polling_key` varchar(128) COMMENT '支持从上下文中获取字段作为key',
 `long_polling_timeout` int(11) COMMENT '',
 `rule` text COMMENT '',
 `bit_options` bigint(20) COMMENT '',
 `clazz` varchar(128) COMMENT '',
 `method` varchar(128) COMMENT '',
 `argument_list` varchar(1024) COMMENT '',
 `return_type` varchar(512) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '函数';
CREATE UNIQUE INDEX `function_namespace_fun` ON `function`(`namespace`,`fun`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `model_relation`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `name` varchar(128) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '关系表';

CREATE TABLE IF NOT EXISTS `model`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `pk` varchar(128) COMMENT '',
 `module` varchar(128) COMMENT '',
 `model` varchar(128) COMMENT '',
 `display_name` varchar(128) COMMENT '',
 `name` varchar(128) COMMENT '',
 `lname` varchar(128) COMMENT '',
 `table` varchar(128) COMMENT '',
 `ds_key` varchar(128) COMMENT '',
 `type` varchar(512) COMMENT '',
 `proxy` varchar(128) COMMENT '',
 `is_relationship` tinyint(1) COMMENT '',
 `summary` varchar(128) COMMENT '',
 `description` text COMMENT '',
 `source` varchar(512) COMMENT '',
 `priority` bigint(20) COMMENT '',
 `data_manager` tinyint(1) COMMENT '是否允许系统根据模型变化自动创建表和更新表',
 `ordering` varchar(128) COMMENT '',
 `sequence_code` varchar(128) COMMENT '序列生成配置编码',
 `un_inherited_fields` varchar(128) COMMENT '',
 `un_inherited_functions` varchar(128) COMMENT '',
 `checks` varchar(128) COMMENT '',
 `rules` varchar(128) COMMENT '',
 `uniques` varchar(128) COMMENT '',
 `indexes` varchar(128) COMMENT '',
 `logic_delete` tinyint(1) COMMENT '',
 `logic_delete_column` varchar(128) COMMENT '',
 `logic_delete_value` varchar(128) COMMENT '',
 `logic_not_delete_value` varchar(128) COMMENT '',
 `optimistic_locker_field` varchar(128) COMMENT '',
 `under_camel` tinyint(1) COMMENT '',
 `capital_mode` tinyint(1) COMMENT '',
 `bit_options` bigint(20) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '模型定义';
CREATE UNIQUE INDEX `model_model` ON `model`(`model`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `test_base_model`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `field` int(11) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '测试模型';

CREATE TABLE IF NOT EXISTS `transaction`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `active` tinyint(1) COMMENT '',
 `namespace` varchar(128) COMMENT '',
 `fun` varchar(128) COMMENT '',
 `ds` varchar(128) COMMENT '',
 `isolation` int(11) COMMENT '',
 `propagation` int(11) COMMENT '',
 `timeout` int(11) COMMENT '',
 `read_only` tinyint(1) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '事务配置';

CREATE TABLE IF NOT EXISTS `test_no_id_model`(
 `field` int(11) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '测试模型';

CREATE TABLE IF NOT EXISTS `field`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `display_name` varchar(128) COMMENT '',
 `lname` varchar(128) COMMENT '代码字段名称',
 `column` varchar(128) COMMENT '数据库字段',
 `summary` varchar(128) COMMENT '',
 `description` text COMMENT '',
 `ttype` varchar(512) COMMENT '字段的业务类型',
 `related_ttype` varchar(512) COMMENT '引用字段的业务类型',
 `ltype` varchar(128) COMMENT '字段java类型',
 `ltype_t` varchar(128) COMMENT '字段的java类型的泛型',
 `related_ltype` varchar(128) COMMENT '引用字段java类型',
 `related_ltype_t` varchar(128) COMMENT '引用字段java类型的泛型',
 `multi` tinyint(1) COMMENT '多值字段',
 `column_definition` varchar(128) COMMENT '数据库字段定义',
 `key_generator` varchar(512) COMMENT '',
 `size` int(11) COMMENT '长度限制',
 `limit` int(11) COMMENT '数量限制',
 `decimal` int(11) COMMENT '小数位数',
 `min` varchar(128) COMMENT '最小值',
 `max` varchar(128) COMMENT '最大值',
 `pk` tinyint(1) COMMENT '主键',
 `pk_index` int(11) COMMENT '主键排序',
 `sequence_code` varchar(128) COMMENT '序列生成配置编码',
 `serialize` varchar(128) COMMENT '序列化',
 `format` varchar(512) COMMENT '时间格式',
 `related` varchar(128) COMMENT '引用字段',
 `source` varchar(512) COMMENT 'BASE是系统创建, MANUAL是人工创建',
 `sudo` varchar(128) COMMENT '免权限控制',
 `dictionary` varchar(128) COMMENT '',
 `options_store` text COMMENT '',
 `store` tinyint(1) COMMENT '存储字段会持久化保存',
 `priority` bigint(20) COMMENT '',
 `extend_priority` int(11) COMMENT '',
 `optimistic_locker` tinyint(1) COMMENT '乐观锁',
 `compute` varchar(512) COMMENT '计算字段的规则',
 `inverse` varchar(128) COMMENT '字段值变化反向计算监听字段值并持久化',
 `search` varchar(512) COMMENT '自定义搜索规则',
 `watch` varchar(128) COMMENT '监听字段',
 `checks` varchar(128) COMMENT '',
 `rules` varchar(128) COMMENT '',
 `default_value` varchar(128) COMMENT '默认值',
 `copied` tinyint(1) COMMENT '',
 `immutable` tinyint(1) COMMENT '',
 `readonly` tinyint(1) COMMENT '',
 `index` tinyint(1) COMMENT '',
 `required` tinyint(1) COMMENT '',
 `unique` tinyint(1) COMMENT '',
 `invisible` tinyint(1) COMMENT '',
 `translate` tinyint(1) COMMENT '',
 `track` varchar(512) COMMENT '',
 `only_column` tinyint(1) COMMENT '',
 `insert_strategy` varchar(512) COMMENT '',
 `batch_strategy` varchar(512) COMMENT '',
 `update_strategy` varchar(512) COMMENT '',
 `where_strategy` varchar(512) COMMENT '',
 `where_condition` varchar(128) COMMENT '',
 `bit_options` bigint(20) COMMENT '',
 `name` varchar(128) COMMENT '',
 `field` varchar(128) COMMENT '字段编码',
 `model` varchar(128) COMMENT '',
 `relation_store` tinyint(1) COMMENT '持久化保存关联关系',
 `relation_fields` varchar(512) COMMENT '自身模型的关联字段',
 `references` varchar(128) COMMENT '',
 `through` varchar(128) COMMENT '',
 `reference_fields` varchar(512) COMMENT '关联模型的关联字段技术名称',
 `through_relation_fields` varchar(128) COMMENT '中间模型的关联字段',
 `through_reference_fields` varchar(128) COMMENT '中间模型的关联模型的关联字段技术名称',
 `page_size` bigint(20) COMMENT '查询分页数量',
 `domain_size` int(11) COMMENT '模型筛选结果数量限制',
 `domain` varchar(1024) COMMENT '',
 `on_update` varchar(512) COMMENT '',
 `on_delete` varchar(512) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '模型字段';
CREATE UNIQUE INDEX `field_model_name` ON `field`(`model`,`name`,`is_deleted`);
CREATE UNIQUE INDEX `field_model_field` ON `field`(`model`,`field`,`is_deleted`);
CREATE UNIQUE INDEX `field_model_column` ON `field`(`model`,`column`,`is_deleted`);

CREATE TABLE IF NOT EXISTS `module_category`(
 `id` bigint(20) AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',
 `name` varchar(128) COMMENT '',
 `code` varchar(128) COMMENT '',
 `parent_code` varchar(128) COMMENT '',
 `sequence` int(11) COMMENT '',
 `description` varchar(512) COMMENT '',
 `visible` tinyint(1) COMMENT '',
 `exclusive` tinyint(1) COMMENT '',
 `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
 `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
 `create_uid` bigint(20) COMMENT '',
 `write_uid` bigint(20) COMMENT '',
 `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',
 PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '应用分类';
CREATE UNIQUE INDEX `module_category_code` ON `module_category`(`code`,`is_deleted`);
CREATE UNIQUE INDEX `module_category_name` ON `module_category`(`name`,`is_deleted`);
