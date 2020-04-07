package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.FieldTrackEnum;

/**
 * 字段追踪枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FieldTrack", displayName = "字段跟踪类型")
public class FieldTrackEnumCls extends BaseEnum<String> {

    protected FieldTrackEnumCls(){}

    public static FieldTrackEnumCls[] values(){
        return BaseEnum.values();
    }

    public static FieldTrackEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static FieldTrackEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static FieldTrackEnumCls NON           = of(FieldTrackEnumCls.class).init(FieldTrackEnum.NON);
    public final static FieldTrackEnumCls ALWAYS        = of(FieldTrackEnumCls.class).init(FieldTrackEnum.ALWAYS);
    public final static FieldTrackEnumCls ON_CHANGE     = of(FieldTrackEnumCls.class).init(FieldTrackEnum.ON_CHANGE);

}
