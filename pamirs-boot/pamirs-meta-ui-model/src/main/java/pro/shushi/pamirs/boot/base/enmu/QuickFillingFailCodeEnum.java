package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Gesi at 18:04 on 2025/9/10
 */
@Base
@Dict(dictionary = "base.QuickFillingFailCodeEnum", displayName = "快速填报失败编码")
public enum QuickFillingFailCodeEnum implements IEnum<Integer> {

    ;

    private final String displayName;

    private final Integer value;

    private final String help;

    QuickFillingFailCodeEnum(int value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

}
