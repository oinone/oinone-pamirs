package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 模型类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.ModelType", displayName = "模型类型")
public class ModelTypeEnumCls extends BaseEnum<String> {

    protected ModelTypeEnumCls(){}

    public static ModelTypeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static ModelTypeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static ModelTypeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static ModelTypeEnumCls STORE         = of(ModelTypeEnumCls.class).init(ModelTypeEnum.STORE);
    public final static ModelTypeEnumCls TRANSIENT     = of(ModelTypeEnumCls.class).init(ModelTypeEnum.TRANSIENT);
    public final static ModelTypeEnumCls ABSTRACT      = of(ModelTypeEnumCls.class).init(ModelTypeEnum.ABSTRACT);
    public final static ModelTypeEnumCls PROXY         = of(ModelTypeEnumCls.class).init(ModelTypeEnum.PROXY);

}
