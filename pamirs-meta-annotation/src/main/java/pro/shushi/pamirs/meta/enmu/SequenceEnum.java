package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 序列生成方式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Sequence", displayName = "序列生成方式")
public enum SequenceEnum implements IEnum<String> {

    AUTO_INCREMENT("AUTO_INCREMENT", "AUTO_INCREMENT", "数据库自增ID"),
    UUID("UUID", "UUID", "UUID"),
    DISTRIBUTION("DISTRIBUTION", "分布式ID", "分布式ID"),
    ;

    private String value;

    private String displayName;

    private String help;

    SequenceEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
