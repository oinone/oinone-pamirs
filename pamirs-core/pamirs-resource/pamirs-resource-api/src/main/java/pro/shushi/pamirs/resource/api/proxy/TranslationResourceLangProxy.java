package pro.shushi.pamirs.resource.api.proxy;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;

@Model.model(TranslationResourceLangProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "目标语言查询", summary = "目标语言查询")
public class TranslationResourceLangProxy extends ResourceLang {

    public static final String MODEL_MODEL = "resource.TranslationResourceLangProxy";

}



