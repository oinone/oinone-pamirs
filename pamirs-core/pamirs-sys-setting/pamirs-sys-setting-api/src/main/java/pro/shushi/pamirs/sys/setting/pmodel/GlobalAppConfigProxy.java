package pro.shushi.pamirs.sys.setting.pmodel;

import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

@Model(displayName = "全局应用配置代理")
@Model.model(GlobalAppConfigProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class GlobalAppConfigProxy extends AppConfig {
    public static final String MODEL_MODEL = "sysSetting.GlobalAppConfigProxy";

}
