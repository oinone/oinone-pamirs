package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.SoftwareLicenseEnum;

/**
 * 软件版权协议枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.SoftwareLicense", displayName = "软件版权协议类型")
public class SoftwareLicenseEnumCls extends BaseEnum<String> {

    protected SoftwareLicenseEnumCls(){}

    public static SoftwareLicenseEnumCls[] values(){
        return BaseEnum.values();
    }

    public static SoftwareLicenseEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static SoftwareLicenseEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static SoftwareLicenseEnumCls GPL2                = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.GPL2);
    public final static SoftwareLicenseEnumCls GPL2ORLATER         = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.GPL2ORLATER);
    public final static SoftwareLicenseEnumCls GPL3                = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.GPL3);
    public final static SoftwareLicenseEnumCls GPL3ORLATER         = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.GPL3ORLATER);
    public final static SoftwareLicenseEnumCls AGPL3               = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.AGPL3);
    public final static SoftwareLicenseEnumCls LGPL3               = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.LGPL3);
    public final static SoftwareLicenseEnumCls ORTHEROSI           = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.ORTHEROSI);
    public final static SoftwareLicenseEnumCls PEEL1               = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.PEEL1);
    public final static SoftwareLicenseEnumCls PPL1                = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.PPL1);
    public final static SoftwareLicenseEnumCls ORTHERPROPRIETARY   = of(SoftwareLicenseEnumCls.class).init(SoftwareLicenseEnum.ORTHERPROPRIETARY);

}
