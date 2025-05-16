package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ID生成方式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.KeyGenerator", displayName = "ID生成方式")
public enum KeyGeneratorEnum implements IEnum<String> {

    NON("NON", "无", "无"),
    AUTO_INCREMENT("AUTO_INCREMENT", "数据库自增ID", "数据库自增ID"),
    DISTRIBUTION("DISTRIBUTION", "分布式ID", "分布式ID"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    KeyGeneratorEnum(String value, String displayName, String help) {
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
