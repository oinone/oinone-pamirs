package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;

/**
 * 模块状态枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.ModuleState", displayName = "模块枚举")
public class ModuleStateEnumCls extends BaseEnum<String> {
    
    protected ModuleStateEnumCls(){}

    public static ModuleStateEnumCls[] values(){
        return BaseEnum.values();
    }

    public static ModuleStateEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static ModuleStateEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static ModuleStateEnumCls UNINSTALLABLE     = of(ModuleStateEnumCls.class).init(ModuleStateEnum.UNINSTALLABLE);
    public final static ModuleStateEnumCls UNINSTALLED       = of(ModuleStateEnumCls.class).init(ModuleStateEnum.UNINSTALLED);
    public final static ModuleStateEnumCls TOINSTALL         = of(ModuleStateEnumCls.class).init(ModuleStateEnum.TOINSTALL);
    public final static ModuleStateEnumCls TOUPGRADE         = of(ModuleStateEnumCls.class).init(ModuleStateEnum.TOUPGRADE);
    public final static ModuleStateEnumCls TOPREVIEW         = of(ModuleStateEnumCls.class).init(ModuleStateEnum.TOPREVIEW);
    public final static ModuleStateEnumCls TOPUBLISH         = of(ModuleStateEnumCls.class).init(ModuleStateEnum.TOPUBLISH);
    public final static ModuleStateEnumCls INSTALLED         = of(ModuleStateEnumCls.class).init(ModuleStateEnum.INSTALLED);
    public final static ModuleStateEnumCls TOREMOVE          = of(ModuleStateEnumCls.class).init(ModuleStateEnum.TOREMOVE);
    public final static ModuleStateEnumCls TORELOAD          = of(ModuleStateEnumCls.class).init(ModuleStateEnum.TORELOAD);
    
}
