package pro.shushi.pamirs.trigger.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Base
@Errors(displayName = "触发器模块错误枚举")
public enum TriggerExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050000, "系统异常"),
    BASE_MODULE_STATE_NOT_SUPPORT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050001, "模块当前的状态不支持操作"),
    BASE_NO_MODULE_FOR_DB_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050002, "数据库中没有当前模块的数据"),
    BASE_JAR_MODULE_NOT_SUPPORT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050003, "应用分组的模块版本落后！"),
    BASE_ACQUIRE_DISTRIBUTE_LOCK_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050004, "抢夺锁失败"),
    BASE_MODULE_NOT_SELF_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050005, "当前模块非自建应用，不允许卸载"),
    BASE_MODULE_AUTHOR_NOT_SUPPORT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050006, "当前模块的作者为数式，不允许删除!"),
    BASE_SQL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050007, "SQL错误"),
    BASE_SPREAD_DEPENDENCY_MODULE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050008, "平铺查询依赖模块异常"),
    BASE_HAS_EXCLUSION_INSTALLED_MODULE(ERROR_TYPE.SYSTEM_ERROR, 10050009, "Oops! 存在已安装互斥模块，启动失败"),
    TRIGGER_SQL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050010, "SQL错误"),
    AUTO_TIMER_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050011, "解析autoTrigger延时时间异常，返回当前时间"),
    EVENT_ZK_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050012, "初始化zk数据失败"),
    TRIGGER_ZK_INIT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050013, "初始化scheduler失败"),
    FUNCTION_NOT_FOUND(ERROR_TYPE.BIZ_ERROR, 10050014, "该执行函数未被加载"),
    TARGET_FUNCTION_NOT_FOUND(ERROR_TYPE.BIZ_ERROR, 10050015, "该目标函数未被加载"),
    TARGET_FUNCTION_LIST_NULL(ERROR_TYPE.BIZ_ERROR, 10050016, "目标函数列表不允许为空"),
    NOTIFY_DEFINITION_IS_EXIST(ERROR_TYPE.BIZ_ERROR, 10050017, "同一执行函数只允许设置一个消息通知，不允许重复创建"),
    NOTIFY_FUNCTION_DEFINITION_IS_EXIST(ERROR_TYPE.BIZ_ERROR, 10050018, "同一目标函数只能接收来自一个执行函数的消息，不允许重复创建"),
    CRON_FUNCTION_PARAMS_CONFIG_ERROR(ERROR_TYPE.BIZ_ERROR, 10050019, "该执行函数传入参数，必须配置ModelModel和dataJson参数"),
    CRON_EXPRESSION_INVALID(ERROR_TYPE.SYSTEM_ERROR, 10050020, "cron表达式无效");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    TriggerExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public ERROR_TYPE type() {
        return type;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
