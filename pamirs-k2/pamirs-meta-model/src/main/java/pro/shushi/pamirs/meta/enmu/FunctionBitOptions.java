package pro.shushi.pamirs.meta.enmu;

/**
 * 函数标记位
 * <p>
 * 2020/7/30 12:50 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public enum FunctionBitOptions {

    DEFAULT_VALUE(0, "初始值"),
    UN_SUPPORT_CLIENT(1L, "不支持客户端"),
    ENABLE_CHECK(1L << 1, "生效请求校验"),
    FORCE_REMOTE(1L << 2, "强制远程调用"),
    VIRTUAL_CALL(1L << 3, "虚拟动作产生的虚拟函数"),

    ;

    private Long option;

    private String remark;

    FunctionBitOptions(long option, String remark) {
        this.option = option;
        this.remark = remark;
    }

    public Long getOption() {
        return option;
    }

    public void setOption(Long option) {
        this.option = option;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
