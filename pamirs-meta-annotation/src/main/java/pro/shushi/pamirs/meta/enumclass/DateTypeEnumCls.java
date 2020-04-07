package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.DateTypeEnum;

/**
 * 时间类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.DateType", displayName = "时间类型")
public class DateTypeEnumCls extends BaseEnum<String> {

    protected DateTypeEnumCls(){}

    public static DateTypeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static DateTypeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static DateTypeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static DateTypeEnumCls DATETIME    = of(DateTypeEnumCls.class).init(DateTypeEnum.DATETIME);
//    public final static DateTypeEnum YEAR        = of(DateTypeEnumCls.class).init(DateTypeEnum.YEAR);
    public final static DateTypeEnumCls DATE        = of(DateTypeEnumCls.class).init(DateTypeEnum.DATE);
    public final static DateTypeEnumCls TIME        = of(DateTypeEnumCls.class).init(DateTypeEnum.TIME);

}
