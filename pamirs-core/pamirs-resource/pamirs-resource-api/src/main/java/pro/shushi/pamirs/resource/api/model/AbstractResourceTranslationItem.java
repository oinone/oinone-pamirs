package pro.shushi.pamirs.resource.api.model;

import com.google.api.client.util.Charsets;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.TranslateDataSourcesEnum;
import pro.shushi.pamirs.resource.api.enmu.TranslateForEnum;
import pro.shushi.pamirs.resource.api.enmu.TranslationApplicationScopeEnum;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Model(displayName = "抽象 翻译资源项")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model.model(AbstractResourceTranslationItem.MODEL_MODEL)
public abstract class AbstractResourceTranslationItem extends IdModel {

    public static final String MODEL_MODEL = "resource.AbstractResourceTranslationItem";

    @Field.String(size = 1024)
    @Field(displayName = "源术语")
    private String origin;

    @Field.String
    @Field(displayName = "源术语编码", summary = "为了支持超长源术语,通过源术语生成该编码,使用编码做唯一键")
    private String originCode;

    @Field.String(size = 1024)
    @Field(displayName = "翻译值")
    private String target;

    @Field.Enum
    @Field(displayName = "翻译方式")
    private TranslateForEnum translateFor;

    @Field.Enum
    @Field(displayName = "数据来源")
    private TranslateDataSourcesEnum dataSource;

    @Field.Enum
    @Field(displayName = "翻译应用范围", translate = true)
    private TranslationApplicationScopeEnum scope;

    @Field(displayName = "源语言")
    @Field.String(size = 16)
    private String resLangCode;

    @Field(displayName = "目标语言")
    @Field.String(size = 16)
    private String langCode;

    @Field.String(size = 64)
    @Field(displayName = "翻译所在模块")
    private String module;

    @Field(displayName = "模型/字典")
    @Field.String
    private String model;

    @Field.Text
    @Field(displayName = "备注")
    private String comments;

    @Field.Boolean
    @Field(displayName = "激活状态", defaultValue = "true", translate = true)
    private Boolean state;

    @Field(displayName = "翻译")
    @Field.many2one
    @Field.Relation(relationFields = {"resLangCode", "langCode", "module"}, referenceFields = {"resLangCode", "langCode", "module"})
    private ResourceTranslation translation;

    @Field(displayName = "应用名称", store = NullableBoolEnum.FALSE)
    @Field.String
    private String moduleName;

    @Field(displayName = "系统字段", defaultValue = "false")
    @Field.Boolean
    private Boolean system;

    public static final Set<String> BACK_END_TRANSLATE_MODEL = Collections.unmodifiableSet(CollectionHelper.<String, Set<String>>newInstance(new HashSet<>())
            .add(ModuleDefinition.MODEL_MODEL)
            .add(UeModule.MODEL_MODEL)
            .add(ModelDefinition.MODEL_MODEL)
            .add(UeModel.MODEL_MODEL)
            .add(ModelField.MODEL_MODEL)
            .add(ViewAction.MODEL_MODEL)
            .build());

    public static final Set<String> BACK_END_DATA_TRANSLATE_MODEL = Collections.unmodifiableSet(CollectionHelper.<String, Set<String>>newInstance(new HashSet<>())
            .add(Menu.MODEL_MODEL)
            .build());

    public void initOriginCode() {
        if (StringUtils.isBlank(this.getOrigin())) {
            // 什么都不做. 更新时有origin才设置originCode
            return;
        }
        this.setOriginCode(Hashing.md5().hashString(this.getOrigin(), Charsets.UTF_8).toString());

    }
}

   