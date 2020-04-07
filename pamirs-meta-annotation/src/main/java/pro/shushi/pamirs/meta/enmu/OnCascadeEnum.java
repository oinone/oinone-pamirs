package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 关联操作枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.OnCascade", displayName = "关联操作")
public enum OnCascadeEnum implements IEnum<String> {

    NO_ACTION("no_action", "无操作", "无操作"),
    SET_NULL("set_null", "设置空值", "设置空值"),
    CASCADE("cascade", "级联操作", "级联操作"),
    RESTRICT("restrict", "限制操作", "限制操作");

    private String value;

    private String displayName;

    private String help;

    OnCascadeEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
