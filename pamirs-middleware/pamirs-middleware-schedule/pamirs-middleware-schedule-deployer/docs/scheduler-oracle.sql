CREATE SEQUENCE "pamirs_schedule_0_seq";
CREATE SEQUENCE "pamirs_schedule_1_seq";
CREATE SEQUENCE "pamirs_schedule_2_seq";
CREATE SEQUENCE "pamirs_schedule_3_seq";
CREATE SEQUENCE "pamirs_schedule_4_seq";
CREATE SEQUENCE "pamirs_schedule_5_seq";
CREATE SEQUENCE "pamirs_schedule_6_seq";
CREATE SEQUENCE "pamirs_schedule_7_seq";
CREATE SEQUENCE "pamirs_schedule_8_seq";
CREATE SEQUENCE "pamirs_schedule_9_seq";
CREATE SEQUENCE "pamirs_schedule_10_seq";
CREATE SEQUENCE "pamirs_schedule_11_seq";
CREATE SEQUENCE "pamirs_schedule_12_seq";
CREATE SEQUENCE "pamirs_schedule_13_seq";

CREATE TABLE "pamirs_schedule_0"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_0_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_0_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_0_task_type_task_status_next_execute_time" ON "pamirs_schedule_0"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_0_biz_id" ON "pamirs_schedule_0"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_0_technical_name" ON "pamirs_schedule_0"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_0" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_0"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_0"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_0"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_0"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_0"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_0"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_0"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_0"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_0"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_0"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_0"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_0"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_0"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_0"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_0"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_0"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_0"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_0"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_0"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_0"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_0"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_0"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_0"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_0"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_0"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_0"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_0"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_0"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_0"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_0"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_0"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_0"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_0"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_0"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_0"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_0"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_0"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_0"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_0"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_0"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_1"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_1_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_1_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_1_task_type_task_status_next_execute_time" ON "pamirs_schedule_1"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_1_biz_id" ON "pamirs_schedule_1"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_1_technical_name" ON "pamirs_schedule_1"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_1" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_1"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_1"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_1"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_1"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_1"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_1"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_1"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_1"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_1"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_1"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_1"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_1"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_1"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_1"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_1"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_1"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_1"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_1"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_1"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_1"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_1"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_1"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_1"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_1"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_1"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_1"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_1"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_1"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_1"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_1"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_1"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_1"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_1"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_1"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_1"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_1"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_1"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_1"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_1"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_1"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_2"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_2_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_2_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_2_task_type_task_status_next_execute_time" ON "pamirs_schedule_2"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_2_biz_id" ON "pamirs_schedule_2"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_2_technical_name" ON "pamirs_schedule_2"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_2" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_2"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_2"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_2"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_2"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_2"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_2"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_2"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_2"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_2"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_2"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_2"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_2"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_2"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_2"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_2"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_2"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_2"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_2"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_2"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_2"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_2"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_2"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_2"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_2"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_2"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_2"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_2"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_2"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_2"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_2"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_2"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_2"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_2"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_2"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_2"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_2"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_2"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_2"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_2"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_2"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_3"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_3_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_3_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_3_task_type_task_status_next_execute_time" ON "pamirs_schedule_3"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_3_biz_id" ON "pamirs_schedule_3"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_3_technical_name" ON "pamirs_schedule_3"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_3" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_3"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_3"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_3"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_3"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_3"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_3"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_3"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_3"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_3"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_3"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_3"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_3"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_3"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_3"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_3"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_3"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_3"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_3"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_3"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_3"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_3"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_3"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_3"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_3"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_3"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_3"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_3"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_3"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_3"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_3"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_3"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_3"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_3"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_3"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_3"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_3"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_3"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_3"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_3"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_3"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_4"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_4_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_4_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_4_task_type_task_status_next_execute_time" ON "pamirs_schedule_4"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_4_biz_id" ON "pamirs_schedule_4"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_4_technical_name" ON "pamirs_schedule_4"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_4" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_4"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_4"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_4"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_4"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_4"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_4"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_4"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_4"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_4"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_4"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_4"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_4"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_4"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_4"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_4"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_4"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_4"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_4"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_4"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_4"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_4"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_4"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_4"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_4"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_4"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_4"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_4"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_4"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_4"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_4"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_4"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_4"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_4"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_4"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_4"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_4"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_4"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_4"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_4"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_4"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_5"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_5_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_5_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_5_task_type_task_status_next_execute_time" ON "pamirs_schedule_5"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_5_biz_id" ON "pamirs_schedule_5"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_5_technical_name" ON "pamirs_schedule_5"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_5" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_5"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_5"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_5"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_5"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_5"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_5"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_5"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_5"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_5"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_5"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_5"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_5"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_5"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_5"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_5"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_5"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_5"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_5"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_5"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_5"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_5"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_5"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_5"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_5"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_5"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_5"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_5"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_5"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_5"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_5"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_5"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_5"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_5"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_5"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_5"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_5"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_5"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_5"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_5"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_5"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_6"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_6_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_6_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_6_task_type_task_status_next_execute_time" ON "pamirs_schedule_6"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_6_biz_id" ON "pamirs_schedule_6"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_6_technical_name" ON "pamirs_schedule_6"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_6" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_6"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_6"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_6"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_6"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_6"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_6"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_6"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_6"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_6"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_6"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_6"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_6"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_6"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_6"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_6"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_6"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_6"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_6"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_6"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_6"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_6"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_6"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_6"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_6"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_6"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_6"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_6"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_6"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_6"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_6"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_6"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_6"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_6"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_6"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_6"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_6"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_6"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_6"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_6"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_6"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_7"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_7_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_7_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_7_task_type_task_status_next_execute_time" ON "pamirs_schedule_7"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_7_biz_id" ON "pamirs_schedule_7"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_7_technical_name" ON "pamirs_schedule_7"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_7" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_7"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_7"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_7"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_7"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_7"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_7"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_7"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_7"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_7"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_7"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_7"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_7"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_7"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_7"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_7"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_7"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_7"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_7"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_7"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_7"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_7"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_7"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_7"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_7"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_7"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_7"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_7"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_7"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_7"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_7"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_7"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_7"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_7"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_7"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_7"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_7"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_7"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_7"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_7"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_7"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_8"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_8_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_8_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_8_task_type_task_status_next_execute_time" ON "pamirs_schedule_8"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_8_biz_id" ON "pamirs_schedule_8"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_8_technical_name" ON "pamirs_schedule_8"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_8" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_8"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_8"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_8"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_8"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_8"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_8"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_8"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_8"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_8"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_8"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_8"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_8"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_8"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_8"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_8"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_8"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_8"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_8"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_8"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_8"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_8"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_8"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_8"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_8"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_8"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_8"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_8"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_8"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_8"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_8"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_8"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_8"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_8"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_8"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_8"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_8"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_8"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_8"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_8"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_8"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_9"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_9_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_9_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_9_task_type_task_status_next_execute_time" ON "pamirs_schedule_9"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_9_biz_id" ON "pamirs_schedule_9"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_9_technical_name" ON "pamirs_schedule_9"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_9" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_9"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_9"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_9"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_9"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_9"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_9"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_9"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_9"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_9"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_9"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_9"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_9"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_9"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_9"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_9"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_9"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_9"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_9"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_9"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_9"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_9"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_9"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_9"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_9"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_9"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_9"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_9"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_9"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_9"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_9"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_9"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_9"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_9"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_9"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_9"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_9"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_9"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_9"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_9"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_9"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_10"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_10_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_10_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_10_task_type_task_status_next_execute_time" ON "pamirs_schedule_10"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_10_biz_id" ON "pamirs_schedule_10"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_10_technical_name" ON "pamirs_schedule_10"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_10" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_10"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_10"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_10"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_10"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_10"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_10"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_10"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_10"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_10"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_10"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_10"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_10"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_10"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_10"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_10"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_10"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_10"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_10"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_10"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_10"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_10"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_10"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_10"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_10"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_10"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_10"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_10"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_10"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_10"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_10"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_10"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_10"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_10"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_10"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_10"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_10"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_10"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_10"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_10"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_10"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_11"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_11_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_11_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_11_task_type_task_status_next_execute_time" ON "pamirs_schedule_11"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_11_biz_id" ON "pamirs_schedule_11"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_11_technical_name" ON "pamirs_schedule_11"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_11" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_11"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_11"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_11"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_11"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_11"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_11"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_11"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_11"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_11"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_11"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_11"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_11"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_11"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_11"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_11"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_11"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_11"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_11"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_11"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_11"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_11"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_11"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_11"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_11"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_11"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_11"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_11"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_11"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_11"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_11"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_11"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_11"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_11"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_11"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_11"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_11"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_11"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_11"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_11"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_11"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_12"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_12_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_12_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_12_task_type_task_status_next_execute_time" ON "pamirs_schedule_12"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_12_biz_id" ON "pamirs_schedule_12"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_12_technical_name" ON "pamirs_schedule_12"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_12" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_12"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_12"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_12"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_12"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_12"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_12"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_12"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_12"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_12"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_12"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_12"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_12"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_12"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_12"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_12"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_12"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_12"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_12"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_12"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_12"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_12"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_12"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_12"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_12"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_12"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_12"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_12"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_12"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_12"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_12"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_12"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_12"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_12"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_12"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_12"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_12"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_12"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_12"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_12"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_12"."write_date" IS '修改时间';

CREATE TABLE "pamirs_schedule_13"(
  "id" BIGINT NOT NULL DEFAULT "pamirs_schedule_13_seq".nextval,
  "technical_name" NVARCHAR2(256),
  "table_num" TINYINT NOT NULL,
  "task_type" NVARCHAR2(256) NOT NULL,
  "remark" NVARCHAR2(1024) DEFAULT NULL,
  "interface_name" NVARCHAR2(256) NOT NULL,
  "method_name" NVARCHAR2(256) NOT NULL,
  "version" NVARCHAR2(256) DEFAULT NULL,
  "group" NVARCHAR2(256) DEFAULT NULL,
  "timeout" INT DEFAULT '3000',
  "tenant" NVARCHAR2(128) NOT NULL,
  "env" NVARCHAR2(128),
  "user_id" BIGINT DEFAULT NULL,
  "username" NVARCHAR2(128),
  "own_sign" NVARCHAR2(128),
  "application" NVARCHAR2(256) NOT NULL,
  "biz_id" BIGINT DEFAULT NULL,
  "parent_biz_id" BIGINT DEFAULT NULL,
  "biz_code" NVARCHAR2(256) DEFAULT NULL,
  "context" CLOB,
  "limit_execute_number" INT DEFAULT NULL,
  "is_cycle" TINYINT DEFAULT '0',
  "period_time_value" INT DEFAULT NULL,
  "period_time_unit" TINYINT DEFAULT NULL,
  "period_time_anchor" TINYINT DEFAULT '-1',
  "limit_retry_number" INT DEFAULT '-1',
  "next_retry_time_value" INT DEFAULT '1',
  "next_retry_time_unit" TINYINT DEFAULT '13',
  "execute_number" BIGINT DEFAULT '0',
  "next_execute_time" BIGINT NOT NULL,
  "last_execute_time" BIGINT DEFAULT NULL,
  "anchor_execute_time" BIGINT NOT NULL,
  "retry_number" INT DEFAULT '0',
  "task_status" TINYINT DEFAULT '0',
  "is_transfer" TINYINT DEFAULT '0',
  "is_canceled" TINYINT DEFAULT '0',
  "error_log" CLOB,
  "is_deleted" BIGINT NOT NULL DEFAULT '0',
  "create_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  "write_date" TIMESTAMP NOT NULL DEFAULT SYSDATE,
  CONSTRAINT "pamirs_schedule_13_PK" PRIMARY KEY ("id","is_deleted")
);

CREATE INDEX "pamirs_schedule_13_task_type_task_status_next_execute_time" ON "pamirs_schedule_13"("task_type","task_status","next_execute_time");
CREATE INDEX "pamirs_schedule_13_biz_id" ON "pamirs_schedule_13"("biz_id");
CREATE UNIQUE INDEX "pamirs_schedule_13_technical_name" ON "pamirs_schedule_13"("technical_name", "is_deleted");

COMMENT ON TABLE "pamirs_schedule_13" IS '任务分发表';
COMMENT ON COLUMN "pamirs_schedule_13"."id" IS '任务参数: 主键Id';
COMMENT ON COLUMN "pamirs_schedule_13"."technical_name" IS '任务参数: 技术名称';
COMMENT ON COLUMN "pamirs_schedule_13"."table_num" IS '控制参数: 数据表number';
COMMENT ON COLUMN "pamirs_schedule_13"."task_type" IS '任务参数: 任务类型';
COMMENT ON COLUMN "pamirs_schedule_13"."remark" IS '任务参数: 备注信息';
COMMENT ON COLUMN "pamirs_schedule_13"."interface_name" IS '函数参数: 接口名称';
COMMENT ON COLUMN "pamirs_schedule_13"."method_name" IS '函数参数: 方法名称';
COMMENT ON COLUMN "pamirs_schedule_13"."version" IS '函数参数: 接口版本';
COMMENT ON COLUMN "pamirs_schedule_13"."group" IS '函数参数: 接口分组';
COMMENT ON COLUMN "pamirs_schedule_13"."timeout" IS '函数参数: 接口调用超时时间';
COMMENT ON COLUMN "pamirs_schedule_13"."tenant" IS '环境参数: 租户';
COMMENT ON COLUMN "pamirs_schedule_13"."env" IS '环境参数: 环境';
COMMENT ON COLUMN "pamirs_schedule_13"."user_id" IS '环境参数: 用户Id';
COMMENT ON COLUMN "pamirs_schedule_13"."username" IS '环境参数: 用户名';
COMMENT ON COLUMN "pamirs_schedule_13"."own_sign" IS '环境参数: 所有者标记(隔离环境域)';
COMMENT ON COLUMN "pamirs_schedule_13"."application" IS '环境参数: 应用名';
COMMENT ON COLUMN "pamirs_schedule_13"."biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_13"."parent_biz_id" IS '订阅者业务id(取模字段)';
COMMENT ON COLUMN "pamirs_schedule_13"."biz_code" IS '业务编码';
COMMENT ON COLUMN "pamirs_schedule_13"."context" IS '任务上下文';
COMMENT ON COLUMN "pamirs_schedule_13"."limit_execute_number" IS '策略参数: 最大执行次数';
COMMENT ON COLUMN "pamirs_schedule_13"."is_cycle" IS '策略参数: 是否循环任务';
COMMENT ON COLUMN "pamirs_schedule_13"."period_time_value" IS '策略参数: 执行周期时间';
COMMENT ON COLUMN "pamirs_schedule_13"."period_time_unit" IS '策略参数: 执行周期时间单位';
COMMENT ON COLUMN "pamirs_schedule_13"."period_time_anchor" IS '策略参数: 执行周期时间锚点';
COMMENT ON COLUMN "pamirs_schedule_13"."limit_retry_number" IS '策略参数: 最大重试次数';
COMMENT ON COLUMN "pamirs_schedule_13"."next_retry_time_value" IS '策略参数: 下次重试间隔时间';
COMMENT ON COLUMN "pamirs_schedule_13"."next_retry_time_unit" IS '策略参数: 下次重试间隔时间单位';
COMMENT ON COLUMN "pamirs_schedule_13"."execute_number" IS '控制参数: 执行次数';
COMMENT ON COLUMN "pamirs_schedule_13"."next_execute_time" IS '控制参数: 下次执行时间';
COMMENT ON COLUMN "pamirs_schedule_13"."last_execute_time" IS '控制参数: 最新执行时间';
COMMENT ON COLUMN "pamirs_schedule_13"."anchor_execute_time" IS '控制参数: 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）';
COMMENT ON COLUMN "pamirs_schedule_13"."retry_number" IS '控制参数: 重试次数';
COMMENT ON COLUMN "pamirs_schedule_13"."task_status" IS '控制参数: 任务状态';
COMMENT ON COLUMN "pamirs_schedule_13"."is_transfer" IS '控制参数: 是否已迁移';
COMMENT ON COLUMN "pamirs_schedule_13"."is_canceled" IS '控制参数: 是否取消';
COMMENT ON COLUMN "pamirs_schedule_13"."error_log" IS '控制参数: 异常堆栈信息,注意要控制大小';
COMMENT ON COLUMN "pamirs_schedule_13"."is_deleted" IS '控制参数: 删除标记（时间戳）';
COMMENT ON COLUMN "pamirs_schedule_13"."create_date" IS '创建时间';
COMMENT ON COLUMN "pamirs_schedule_13"."write_date" IS '修改时间';
