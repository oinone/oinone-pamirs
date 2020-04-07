package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 继承类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.InheritedType", displayName = "继承类型")
public enum InheritedTypeEnum implements IEnum<String> {

    ABSTRACT("abstract", "抽象基类", "抽象基类"),
    EXTENDS("extends", "扩展继承", "扩展继承，同表"),
    MULTI("multi", "多表继承", "多表继承，父模型不变，子模型获得父模型字段、模型约束和动作生成新的模型，不同表"),
    PROXY("proxy", "代理继承", "代理继承"),
    ;

    private String value;

    private String displayName;

    private String help;

    InheritedTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
