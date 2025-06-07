package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.proxy.TranslationModuleDefinitionProxy;
import pro.shushi.pamirs.resource.api.proxy.TranslationResourceLangProxy;

import java.util.List;

/**
 * ResourceTranslation
 *
 * @author yakir on 2019/06/27 17:17.
 */
@Model.model(ResourceTranslation.MODEL_MODEL)
@Model.Advanced(name = "resourceTranslation", unique = {"module,resLangCode,langCode"})
@Model(displayName = "翻译资源", labelFields = "module")
public class ResourceTranslation extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceTranslation";

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "翻译所在模块")
    private TranslationModuleDefinitionProxy moduleDefinition;

    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "翻译所在模块名称")
    private String module;

    @Field.Boolean
    @Field(displayName = "是否是字典", defaultValue = "false", store = NullableBoolEnum.FALSE)
    private Boolean isDict;

    @Field.many2one
    @Field.Relation(relationFields = {"model"}, referenceFields = {"model"})
    @Field(displayName = "模型")
    private ModelDefinition modelDefinition;

    @Field.many2one
    @Field.Relation(relationFields = {"model"}, referenceFields = {"dictionary"})
    @Field(displayName = "字典")
    private DataDictionary dataDictionary;

    @Field.String
    @Field(displayName = "模型/字典")
    private String model;

    @Field.many2one
    @Field.Relation(relationFields = {"resLangCode"}, referenceFields = {"code"})
    @Field(displayName = "源语言")
    private ResourceLang resLang;

    @Field.String
    @Field(displayName = "源语言编码")
    private String resLangCode;

    @Field.many2one
    @Field.Relation(relationFields = {"langCode"}, referenceFields = {"code"})
    @Field(displayName = "目标语言")
    private TranslationResourceLangProxy lang;

    @Field.String
    @Field(displayName = "目标语言编码")
    private String langCode;

    @Field.Text
    @Field(displayName = "备注")
    private String comments;

    @Field.Boolean
    @Field(displayName = "激活状态", defaultValue = "true")
    private Boolean state;

    @Field.one2many
    @Field.Relation(relationFields = {"resLangCode", "langCode", "module"}, referenceFields = {"resLangCode", "langCode", "module"})
    @Field(displayName = "翻译项")
    private List<ResourceTranslationItem> translationItems;

    @Field(displayName = "应用名称", store = NullableBoolEnum.FALSE)
    @Field.String
    private String moduleName;

    @Field.String(size = 512)
    @Field(displayName = "远程资源URL")
    private String remoteUrl;
}
