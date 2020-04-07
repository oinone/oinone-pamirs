package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;

/**
 * 转换方式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Serialize", displayName = "序列化方式")
public class SerializeEnumCls extends BaseEnum<String> {

    protected SerializeEnumCls(){}

    public static SerializeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static SerializeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static SerializeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static SerializeEnumCls JSON      = of(SerializeEnumCls.class).init(SerializeEnum.JSON);
    public final static SerializeEnumCls COMMA     = of(SerializeEnumCls.class).init(SerializeEnum.COMMA);
    public final static SerializeEnumCls DOT       = of(SerializeEnumCls.class).init(SerializeEnum.DOT);
}
