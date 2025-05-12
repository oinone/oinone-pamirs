package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 函数类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionType", displayName = "函数类型")
public enum FunctionTypeEnum implements BitEnum {

    CREATE(1L, "增", "新增"),
    DELETE(2L, "删", "删除"),
    UPDATE(4L, "改", "更新"),
    QUERY(8L, "查", "查询");

    private final Long value;
    private final String displayName;
    private final String help;

    FunctionTypeEnum(Long value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public Long value() {
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