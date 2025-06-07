package pro.shushi.pamirs.translate.model;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.TranslationApplicationScopeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.translate.enmu.TranslationMethodEnum;

@Model(displayName = "导入文件")
@Model.model(AbstractTranslationItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
public class AbstractTranslationItem extends TransientModel {

    public static final String MODEL_MODEL = "translation.AbstractTranslationItem";

    @Field.many2one
    @Field(displayName = "导入文件", required = true)
    private PamirsFile file;

    @Field(displayName = "模版ID")
    private Long workbookId;

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "翻译所在模块")
    private ModuleDefinition moduleDefinition;

    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "应用")
    private String module;

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
    private ResourceLang lang;

    @Field.String
    @Field(displayName = "目标语言编码")
    private String langCode;

    @Field.String(size = 1024)
    @Field.Advanced(columnDefinition = "varchar(1024)")
    @Field(displayName = "源术语")
    private String origin;

    @Field.String(size = 1024)
    @Field.Advanced(columnDefinition = "varchar(1024)")
    @Field(displayName = "翻译值")
    private String target;

    @Field.Boolean
    @Field(displayName = "激活状态", defaultValue = "true")
    private Boolean state;

    @Field.Enum
    @Field(displayName = "翻译方式")
    private TranslationMethodEnum translateFor;

    @Field.String
    @Field(displayName = "翻译项应用范围")
    private TranslationApplicationScopeEnum scope;

    @Field.Text
    @Field(displayName = "备注", store = NullableBoolEnum.FALSE)
    private String comments;
}
