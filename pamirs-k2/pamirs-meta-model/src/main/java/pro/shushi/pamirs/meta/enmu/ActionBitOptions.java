package pro.shushi.pamirs.meta.enmu;

/**
 * Action标记位
 * <p>
 * 2023/12/16
 *
 * @author wangxian@shushi.pro
 * @version 1.0.0
 */
public enum ActionBitOptions {

    DEFAULT_VALUE(0, "初始值"),
    UI_ACTION_RESERVE(1L, "UI创建的Action保留");

    private Long option;

    private String remark;

    ActionBitOptions(long option, String remark) {
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
