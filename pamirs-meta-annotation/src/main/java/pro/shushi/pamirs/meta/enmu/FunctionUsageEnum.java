package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 函数类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionUsage", displayName = "函数用途")
public enum FunctionUsageEnum implements IEnum<String> {

    READ("READ", "READ", "读"),
    WRITE("WRITE", "WRITE", "写");

    private String value;

    private String displayName;

    private String help;

    FunctionUsageEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
