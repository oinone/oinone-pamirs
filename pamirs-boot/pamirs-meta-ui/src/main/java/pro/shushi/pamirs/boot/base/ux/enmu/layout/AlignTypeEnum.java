package pro.shushi.pamirs.boot.base.ux.enmu.layout;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 对齐方式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.AlignType", displayName = "对齐方式枚举")
public enum AlignTypeEnum implements IEnum<String> {

    START("start", "向开始位置对齐", "向开始位置对齐"),
    CENTER("center", "居中对齐", "居中对齐"),
    END("end", "向结束位置对齐", "向结束位置对齐"),
    STRETCH("stretch", "伸缩对齐", "伸缩对齐");

    private final String displayName;

    private final String value;

    private final String help;

    AlignTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
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
