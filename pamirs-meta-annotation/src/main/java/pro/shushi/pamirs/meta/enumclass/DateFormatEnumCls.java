package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;

/**
 * 时间格式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.DateFormat", displayName = "时间格式")
public class DateFormatEnumCls extends BaseEnum<String> {

    protected DateFormatEnumCls(){}

    public static DateFormatEnumCls[] values(){
        return BaseEnum.values();
    }

    public static DateFormatEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static DateFormatEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static DateFormatEnumCls DATETIME      = of(DateFormatEnumCls.class).init(DateFormatEnum.DATETIME);
    public final static DateFormatEnumCls DATE          = of(DateFormatEnumCls.class).init(DateFormatEnum.DATE);
    public final static DateFormatEnumCls TIME          = of(DateFormatEnumCls.class).init(DateFormatEnum.TIME);
    public final static DateFormatEnumCls YEAR          = of(DateFormatEnumCls.class).init(DateFormatEnum.YEAR);

}
