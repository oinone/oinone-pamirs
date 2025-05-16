package pro.shushi.pamirs.meta.enmu;

/**
 * 字段标记位
 * <p>
 * 2020/7/30 12:50 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public enum FieldBitOptions {

    DEFAULT_VALUE(0L, "初始值"),
    UN_SUPPORT_CLIENT(1L, "不支持客户端"),

    ;

    private Long option;

    private String remark;

    FieldBitOptions(long option, String remark) {
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
