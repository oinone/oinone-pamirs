package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.RtypeEnum;

/**
 * 字段关系类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Rtype", displayName = "关系类型")
public class RtypeEnumCls extends BaseEnum<String> {

    protected RtypeEnumCls(){}

    public static RtypeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static RtypeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static RtypeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    // 关系类型
    public final static TtypeEnumCls O2O = of(TtypeEnumCls.class).init(RtypeEnum.O2O);
    public final static TtypeEnumCls O2M = of(TtypeEnumCls.class).init(RtypeEnum.O2M);
    public final static TtypeEnumCls M2O = of(TtypeEnumCls.class).init(RtypeEnum.M2O);
    public final static TtypeEnumCls M2M = of(TtypeEnumCls.class).init(RtypeEnum.M2M);

}