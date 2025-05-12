package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.List;

import static pro.shushi.pamirs.meta.domain.model.DataDictionary.MODEL_MODEL;

/**
 * 数据字典
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 7)
@Base
@Model.Advanced(priority = 12)
@Model.model(MODEL_MODEL)
@Model(displayName = "数据字典", summary = "数据字典", labelFields = "displayName")
public class DataDictionary extends MetaBaseModel implements MetaCheckConstants {

    private static final long serialVersionUID = -5696118573647520896L;

    public static final String MODEL_MODEL = "base.DataDictionary";

    @Base
    @Validation(check = checkModuleModule)
    @Field.String
    @Field(displayName = "应用", summary = "应用模块", required = true)
    private String module;

    @Base
    @Field.String
    @Field(displayName = "字典名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "字典编码", required = true, unique = true)
    private String dictionary;

    @Base
    @Field.String
    @Field(displayName = "api名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "枚举代码名称")
    private String lname;

    @Base
    @Field.String
    @Field(displayName = "描述摘要")
    private String summary;

    @Base
    @Field.Enum
    @Field(displayName = "枚举类型")
    private TtypeEnum valueType;

    @Base
    @Field.Advanced(columnDefinition = "text")
    @Field.one2many
    @Field(displayName = "数据字典项", store = NullableBoolEnum.TRUE)
    private List<DataDictionaryItem> options;

    @Base
    @Field.Integer
    @Field(displayName = "类型")
    private Integer type;

    @Base
    @Field.Boolean
    @Field(displayName = "二进制类型")
    private Boolean bit;

    @Base
    @Field.Enum
    @Field(displayName = "状态", defaultValue = "true")
    private ActiveEnum state;

    @Base
    @Field.Enum
    @Field(displayName = "可见", defaultValue = "true")
    private ActiveEnum show;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = MetaDefaultConstants.PRIORITY_VALUE_STRING)
    private Long priority;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}
