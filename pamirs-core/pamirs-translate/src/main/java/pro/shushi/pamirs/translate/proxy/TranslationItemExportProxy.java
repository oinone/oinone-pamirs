package pro.shushi.pamirs.translate.proxy;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.resource.api.proxy.TranslationResourceLangProxy;
import pro.shushi.pamirs.translate.enmu.TranslationStatusEnum;

@Model(displayName = "导出翻译文件")
@Model.model(TranslationItemExportProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class TranslationItemExportProxy extends ResourceTranslationItem {
    public static final String MODEL_MODEL = "translation.TranslationItemExportProxy";

    @Field(displayName = "是否已翻译")
    private TranslationStatusEnum isTranslate;

    @Field(displayName = "源术语包含")
    private String resLangInclude;

    @Field(displayName = "翻译值包含")
    private String targetInclude;

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "翻译所在模块")
    private TranslationModuleProxy moduleDefinition;

    @Field(displayName = "rsql")
    private String rsql;


    @Field.many2one
    @Field.Relation(relationFields = {"resLangCode"}, referenceFields = {"code"})
    @Field(displayName = "源语言")
    private ResourceLang resLang;

    @Field.many2one
    @Field.Relation(relationFields = {"langCode"}, referenceFields = {"code"})
    @Field(displayName = "目标语言")
    private TranslationResourceLangProxy lang;

}

