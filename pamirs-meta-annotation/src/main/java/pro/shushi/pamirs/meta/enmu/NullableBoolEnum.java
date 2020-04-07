package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 布尔枚举（允许为null）
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.NullableBool", displayName = "可空布尔类型")
public enum NullableBoolEnum implements IEnum<Boolean> {

    NULL(null, "未选择", "空"),
    TRUE(true, "是", "真"),
    FALSE(false, "否", "假");

    public final Boolean value;

    private String displayName;

    private String help;

    NullableBoolEnum(Boolean value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
