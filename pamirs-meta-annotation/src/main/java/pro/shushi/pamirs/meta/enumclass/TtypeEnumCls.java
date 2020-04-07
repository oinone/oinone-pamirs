package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Arrays;

/**
 * 字段类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Ttype", displayName = "业务类型")
public final class TtypeEnumCls extends RtypeEnumCls {

    protected TtypeEnumCls(){}

    public static TtypeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static TtypeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static TtypeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    // 基本类型
    public final static TtypeEnumCls INTEGER        = of(TtypeEnumCls.class).init(TtypeEnum.INTEGER);
    public final static TtypeEnumCls FLOAT          = of(TtypeEnumCls.class).init(TtypeEnum.FLOAT);
    public final static TtypeEnumCls BOOLEAN        = of(TtypeEnumCls.class).init(TtypeEnum.BOOLEAN);
    public final static TtypeEnumCls STRING         = of(TtypeEnumCls.class).init(TtypeEnum.STRING);
    public final static TtypeEnumCls TEXT           = of(TtypeEnumCls.class).init(TtypeEnum.TEXT);
    public final static TtypeEnumCls DATETIME       = of(TtypeEnumCls.class).init(TtypeEnum.DATETIME);
    public final static TtypeEnumCls DATE           = of(TtypeEnumCls.class).init(TtypeEnum.DATE);
    public final static TtypeEnumCls TIME           = of(TtypeEnumCls.class).init(TtypeEnum.TIME);

    // 复杂类型
    public final static TtypeEnumCls MONEY          = of(TtypeEnumCls.class).init(TtypeEnum.MONEY);
    public final static TtypeEnumCls HTML           = of(TtypeEnumCls.class).init(TtypeEnum.HTML);
    public final static TtypeEnumCls RELATED        = of(TtypeEnumCls.class).init(TtypeEnum.RELATED);
    public final static TtypeEnumCls ENUM           = of(TtypeEnumCls.class).init(TtypeEnum.ENUM);

    public static boolean isRelationType(TtypeEnumCls ttype){
        return O2M.equals(ttype) || M2O.equals(ttype) || M2M.equals(ttype) || O2O.equals(ttype);
    }

}