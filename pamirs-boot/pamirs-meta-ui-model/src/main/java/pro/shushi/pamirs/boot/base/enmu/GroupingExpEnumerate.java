package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 分组异常
 *
 * @author Gesi at 10:44 on 2025/9/1
 */
public enum GroupingExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10067000, "系统异常"),
    MODEL_NOT_FIND(ERROR_TYPE.SYSTEM_ERROR, 10067001, "模型找不到"),
    LAZY_LOAD_PATHS_IS_NULL(ERROR_TYPE.SYSTEM_ERROR, 10067002, "懒加载路径为空"),
    ;

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    GroupingExpEnumerate(ERROR_TYPE type, int code, String msg) {
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
