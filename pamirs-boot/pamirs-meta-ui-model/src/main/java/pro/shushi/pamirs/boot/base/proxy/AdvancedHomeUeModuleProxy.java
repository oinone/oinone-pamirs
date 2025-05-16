package pro.shushi.pamirs.boot.base.proxy;

import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author ranjingnian
 */
@Model.model(AdvancedHomeUeModuleProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "首页配置应用")
public class AdvancedHomeUeModuleProxy extends UeModule {

    public static final String MODEL_MODEL = "base.AdvancedHomeUeModuleProxy";

}
