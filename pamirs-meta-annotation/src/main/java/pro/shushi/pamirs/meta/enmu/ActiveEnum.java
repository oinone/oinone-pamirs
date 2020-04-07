package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 激活状态枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Active", displayName = "激活状态")
public enum ActiveEnum implements IEnum<Boolean> {

    ACTIVE(true, "激活", "激活"),
    INACTIVE(false, "无效", "无效");

    private Boolean value;

    private String displayName;

    private String help;

    ActiveEnum(Boolean value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
