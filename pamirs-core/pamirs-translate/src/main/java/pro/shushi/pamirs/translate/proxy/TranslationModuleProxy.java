package pro.shushi.pamirs.translate.proxy;

import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

@Model.model(TranslationModuleProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "树形结构查询应用", summary = "树形结构查询应用")
public class TranslationModuleProxy extends UeModule {

    public static final String MODEL_MODEL = "translate.TranslationModuleProxy";

}



