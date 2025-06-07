package pro.shushi.pamirs.resource.api.proxy;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

@Model.model(TranslationModuleDefinitionProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "翻译所在模块", summary = "翻译所在模块")
public class TranslationModuleDefinitionProxy extends ModuleDefinition {

    public static final String MODEL_MODEL = "translate.TranslationModuleDefinitionProxy";

}



