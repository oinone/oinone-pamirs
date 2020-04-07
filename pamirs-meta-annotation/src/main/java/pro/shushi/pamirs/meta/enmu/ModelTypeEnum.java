package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 模型类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.ModelType", displayName = "模型类型")
public enum ModelTypeEnum implements IEnum<String> {

    STORE("store", "存储模型", "存储模型"),
    TRANSIENT("transient", "传输模型", "传输模型"),
    ABSTRACT("abstract", "抽象模型", "抽象模型"),
    PROXY("proxy", "代理模型", "代理模型")
    ;

    private String value;

    private String displayName;

    private String help;

    ModelTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
    }

}
