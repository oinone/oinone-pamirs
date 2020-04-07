package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 字段关系类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Ttype", displayName = "关系类型")
public enum RtypeEnum implements IEnum<String> {

    // 关系类型
    O2O("o2o", "一对一", "一对一"),
    O2M("o2m", "一对多", "一对多"),
    M2O("m2o", "多对一", "多对一"),
    M2M("m2m", "多对多", "多对多"),

    ;

    private String value;

    private String displayName;

    private String help;

    RtypeEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help();
    }

}