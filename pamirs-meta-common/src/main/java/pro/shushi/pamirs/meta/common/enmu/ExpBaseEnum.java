package pro.shushi.pamirs.meta.common.enmu;

import pro.shushi.pamirs.meta.common.util.UnsafeUtil;

public interface ExpBaseEnum extends IEnum<Integer> {

    enum ERROR_TYPE {

        SYSTEM_ERROR("系统错误"),
        GENERIC_ERROR("普通错误"),
        REMOTE_ERROR("远程错误"),
        BIZ_ERROR("业务错误"),
        SECURITY_ERROR("权限错误"),
        DATA_ERROR("数据错误"),
        LOGIC_ERROR("逻辑错误"),

        ;

        private String remark;

        ERROR_TYPE(String remark) {
            this.remark = remark;
        }

        public String getType() {
            return name();
        }

        public String getRemark() {
            return remark;
        }

    }

    enum LEVEL {

        DEBUG("调试"),
        INFO("提示"),
        WARN("警告"),
        ERROR("错误"),
        ;

        private String level;

        LEVEL(String level) {
            this.level = level;
        }

        public String getLevel() {
            return level;
        }
    }

    default ERROR_TYPE type() {
        return (ERROR_TYPE) UnsafeUtil.getValue(this, "type");
    }

    default int code() {
        return (Integer) UnsafeUtil.getValue(this, "code");
    }

    default String msg() {
        return (String) UnsafeUtil.getValue(this, "msg");
    }

    @Override
    default String displayName() {
        return name();
    }

    @Override
    default Integer value() {
        return code();
    }

    @Override
    default String help() {
        return msg();
    }

}
