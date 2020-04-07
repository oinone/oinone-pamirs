package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 系统来源枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.SystemSource", displayName = "系统来源")
public enum SystemSourceEnum implements IEnum<String> {

    BASE("BASE", "系统原生", "系统原生"),
    EXTEND("EXTEND", "扩展继承生成", "扩展继承生成"),
    INHERITED("INHERITED", "多表继承生成", "多表继承生成"),
    ABSTRACT("ABSTRACT", "抽象基类生成", "抽象基类生成"),
    RELATION("RELATION", "关联关系生成", "关联关系生成"),
    MANUAL("MANUAL", "业务新增", "业务新增");

    private String value;

    private String displayName;

    private String help;

    SystemSourceEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
