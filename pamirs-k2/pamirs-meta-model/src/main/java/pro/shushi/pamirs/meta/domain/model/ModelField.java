package pro.shushi.pamirs.meta.domain.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableConfig;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.DOT;
import static pro.shushi.pamirs.meta.domain.model.ModelField.MODEL_MODEL;

/**
 * 字段定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 5, core = java.lang.reflect.Field.class)
@Base
@Model.Advanced(name = "field", priority = 10, unique = {"model,name", "model,field"})
@Model.model(MODEL_MODEL)
@Model(displayName = "模型字段", summary = "模型字段", labelFields = "displayName")
public class ModelField extends Relation implements MetaCheckConstants {

    private static final long serialVersionUID = 9128618382873717565L;

    public static final String MODEL_MODEL = "base.Field";

    public static final String UE_MODEL_MODEL = "base.UeField";

    @Base
    @Field.String
    @Field(displayName = "字段名称", required = true, translate = true)
    private String displayName;

    @Base
    @Validation(check = checkFieldName)
    @Field.String
    @Field(displayName = "代码字段名称", summary = "代码字段名称", invisible = true)
    private String lname;

    @Base
    @Validation(check = checkColumnName)
    @Field.String
    @Field(displayName = "数据库字段", summary = "数据库字段", invisible = true)
    private String column;

    @Base
    @Field.String(size = 500)
    @Field(displayName = "备注", summary = "存储备注")
    private String remark;

    @Base
    @Field.String(size = 500)
    @Field(displayName = "简介", summary = "描述摘要")
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述", summary = "描述详情")
    private String description;

    @Base
    @Field.Enum
    @Field(required = true, displayName = "字段类型", summary = "字段的业务类型")
    private TtypeEnum ttype;

    @Base
    @Field.Enum
    @Field(displayName = "引用字段类型", summary = "引用字段的业务类型")
    private TtypeEnum relatedTtype;

    @Base
    @Validation(check = checkLtype)
    @Field.String(size = 256)
    @Field(displayName = "字段后台类型", summary = "字段java类型", invisible = true)
    private String ltype;

    @Base
    @Validation(check = checkLtypeT)
    @Field.String(size = 256)
    @Field(displayName = "字段泛化类型", summary = "字段的java类型的泛型", invisible = true)
    private String ltypeT;

    @Base
    @Field.Boolean
    @Field(displayName = "多值字段", summary = "多值字段")
    private Boolean multi;

    @Base
    @Field.String
    @Field(summary = "数据库字段定义", displayName = "数据库字段定义", invisible = true)
    private String columnDefinition;

    @Base
    @Field.Enum
    @Field(displayName = "ID生成策略", invisible = true)
    private KeyGeneratorEnum keyGenerator;

    @Base
    @Field.Integer
    @Field(displayName = "字段长度", summary = "长度限制")
    private Integer size;

    @Base
    @Field.Integer
    @Field(displayName = "数量限制", summary = "数量限制")
    private Integer limit;

    @Base
    @Field.Integer
    @Field(displayName = "精度", summary = "小数位数")
    private Integer decimal;

    @Base
    @Field.String
    @Field(displayName = "最小值", summary = "最小值")
    private String min;

    @Base
    @Field.String
    @Field(displayName = "最大值", summary = "最大值")
    private String max;

    @Base
    @Field.Boolean
    @Field(displayName = "主键", summary = "主键", defaultValue = "false")
    private Boolean pk;

    @Base
    @Field.Integer
    @Field(displayName = "主键排序", summary = "主键排序")
    private Integer pkIndex;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "sequenceCode", referenceFields = "code")
    @Field(summary = "序列生成配置", displayName = "序列生成配置")
    private SequenceConfig sequenceConfig;

    @Base
    @Field.String(size = 256)
    @Field(displayName = "序列生成配置编码", summary = "序列生成配置编码")
    private String sequenceCode;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"#serialize#", "requestSerialize"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=serialize,ttype=eq=@{argTtypes}")
    @Field(summary = "请求序列化函数", displayName = "请求序列化函数")
    private FunctionDefinition requestSerializeFunction;

    @Base
    @Field(summary = "请求序列化函数编码", displayName = "请求序列化函数编码", invisible = true)
    private String requestSerialize;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"#serialize#", "storeSerialize"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=serialize,ttype=eq=@{argTtypes}")
    @Field(summary = "存储序列化函数", displayName = "存储序列化函数")
    private FunctionDefinition storeSerializeFunction;

    @Base
    @Field(summary = "存储序列化函数编码", displayName = "存储序列化函数编码", invisible = true)
    private String storeSerialize;

    @Base
    @Field.Enum
    @Field(summary = "时间格式", displayName = "时间格式")
    private DateFormatEnum format;

    @Base
    @Field.one2many(inverse = true)
    @Field.Relation(relationFields = {"model", "related"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "引用字段列表")
    private List<ModelField> relatedList;

    @Base
    @Validation(check = checkDotExpression)
    @Field(displayName = "引用字段", summary = "引用字段", serialize = DOT, store = NullableBoolEnum.TRUE)
    private List<String> related;

    @Base
    @Field.String
    @Field(displayName = "免权限控制", summary = "免权限控制")
    private List<String> sudo;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "dictionary")
    @Field(displayName = "数据字典")
    private DataDictionary selection;

    @Base
    @Field(displayName = "数据字典编码", invisible = true)
    private String dictionary;

    @Base
    @Field.Advanced(columnDefinition = "text")
    @Field.one2many
    @Field(displayName = "可选项", invisible = true, store = NullableBoolEnum.TRUE)
    private List<DataDictionaryItem> options;

    @Base
    @Field.Boolean
    @Field(displayName = "存储", summary = "存储字段会持久化保存")
    private Boolean store;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = MetaDefaultConstants.PRIORITY_VALUE_STRING)
    private Long priority;

    @Base
    @Field.Boolean
    @Field(displayName = "乐观锁", summary = "乐观锁", defaultValue = "false")
    private Boolean optimisticLocker;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "compute"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=@{model},name=eq=@{compute}")
    @Field(summary = "计算函数", displayName = "计算函数")
    private FunctionDefinition computeFunction;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "计算函数编码", summary = "计算函数编码")
    private String compute;

    @Base
    @Field.String
    @Field(displayName = "默认值", summary = "默认值")
    private String defaultValue;

    @Base
    @Field.Boolean
    @Field(displayName = "可拷贝", defaultValue = "true")
    private Boolean copied;

    @Base
    @Field.Boolean
    @Field(displayName = "不可变更", defaultValue = "false")
    private Boolean immutable;

    @Base
    @Field.Boolean
    @Field(displayName = "索引", defaultValue = "false")
    private Boolean index;

    @Base
    @Field.Boolean
    @Field(displayName = "唯一", defaultValue = "false")
    private Boolean unique;

    @Base
    @Field.Boolean
    @Field(displayName = "是否必填", defaultValue = "false")
    private Boolean required;

    @Base
    @Field.String
    @Field(displayName = "条件必填", summary = "required为true，则必填；若required为false，则按条件判断")
    private String requiredCondition;

    @Base
    @Field.Boolean
    @Field(displayName = "不可见", defaultValue = "false")
    private Boolean invisible;

    @Base
    @Field.Boolean
    @Field(displayName = "翻译", defaultValue = "false")
    private Boolean translate;

    @Base
    @Field.Enum
    @Field(displayName = "字段追踪", defaultValue = "no_track")
    private FieldTrackEnum track;

    @Base
    @Field.Boolean
    @Field(displayName = "不支持列名格式化", invisible = true, defaultValue = "true")
    private Boolean onlyColumn;

    @Base
    @Field.Enum
    @Field(displayName = "插入策略", invisible = true, defaultValue = "default")
    private FieldStrategyEnum insertStrategy;

    @Base
    @Field.Enum
    @Field(displayName = "批量策略", invisible = true, defaultValue = "not_change")
    private FieldStrategyEnum batchStrategy;

    @Base
    @Field.Enum
    @Field(displayName = "更新策略", invisible = true, defaultValue = "default")
    private FieldStrategyEnum updateStrategy;

    @Base
    @Field.Enum
    @Field(displayName = "条件策略", invisible = true, defaultValue = "default")
    private FieldStrategyEnum whereStrategy;

    @Base
    @Field.String
    @Field(displayName = "默认查询条件", invisible = true, defaultValue = "%s = #{%s}")
    private String whereCondition;

    @Base
    @Field.Enum
    @Field(displayName = "字符集", invisible = true)
    private CharsetEnum charset;

    @Base
    @Field.Enum
    @Field(displayName = "字符集校验规则", invisible = true)
    private CollationEnum collation;

    @Base
    @Field(displayName = "扩展配置", store = NullableBoolEnum.TRUE, invisible = true)
    private ModelFieldConf extendConfig;

    @Base
    @Field(displayName = "可见", defaultValue = "true", summary = "如果show为false，则表示废弃但仍可使用")
    private ActiveEnum show;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    public ModelField construct(ModelField data) {
        construct0(data);
        if (null != data.getTtype()) {
            defaultRelationStore(data);
        }
        return data;
    }

    private Boolean relatedInternalStore = Boolean.TRUE;

    public void construct0(ModelField data) {
        if (null == data.getStore() && null != data.getTtype()) {
            ModelConfig currentModel = PamirsSession.getContext().getModelConfig(data.getModel());
            if (ModelTypeEnum.TRANSIENT.equals(currentModel.getType())) {
                data.setStore(false);
            } else {
                data.setStore(Optional.of(data).map(ModelField::getStore)
                        .orElse(!TtypeEnum.isRelationType(data.getTtype()) && !TtypeEnum.RELATED.value().equals(data.getTtype().value())));
            }
        }
        if (null != data.getStore() && data.getStore() && StringUtils.isBlank(data.getColumn())) {
            data.setColumn(Optional.of(data).map(ModelField::getColumn).orElse(ModelField.generateColumn(data.getModel(), data.getName(), null)));
        }
        if (null == data.getSize() && Integer.class.getName().equals(data.getLtype())) {
            data.setSize(TypeProcessor.DEFAULT_INTEGER);
        }
        if (null == data.getFormat()) {
            if (TtypeEnum.DATETIME.name().equals(data.getTtype().name())) {
                data.setFormat(DateFormatEnum.DATETIME);
            } else if (TtypeEnum.DATE.name().equals(data.getTtype().name())) {
                data.setFormat(DateFormatEnum.DATE);
            } else if (TtypeEnum.TIME.name().equals(data.getTtype().name())) {
                data.setFormat(DateFormatEnum.TIME);
            } else if (TtypeEnum.YEAR.name().equals(data.getTtype().name())) {
                data.setFormat(DateFormatEnum.YEAR);
            }
        }
    }

    @SuppressWarnings("unused")
    public static String generateColumn(ModelDefinition modelDefinition, String fieldName, String column) {
        PamirsTableConfig pamirsTableConfig = PamirsTableInfo.fetchPamirsTableConfig(modelDefinition);
        return generateColumn(fieldName, column, pamirsTableConfig.getTableNameCaseSensitive(),
                pamirsTableConfig.getUnderCamel(), pamirsTableConfig.getCapitalMode());
    }

    public static String generateColumn(String model, String fieldName, String column) {
        PamirsTableConfig pamirsTableConfig = PamirsTableInfo.fetchPamirsTableConfig(model);
        return generateColumn(fieldName, column, pamirsTableConfig.getTableNameCaseSensitive(),
                pamirsTableConfig.getUnderCamel(), pamirsTableConfig.getCapitalMode());
    }

    public static String generateColumn(String fieldName, String column, boolean tableNameCaseSensitive,
                                        Boolean underCamel, Boolean capitalMode) {
        if (StringUtils.isNotBlank(column)) {
            return column;
        }
        column = fieldName;
        if (underCamel) {
            /* 开启字段下划线申明 */
            column = PStringUtils.fieldName2Column(column);
        }
        if (!tableNameCaseSensitive) {
            column = column.toLowerCase();
        } else if (capitalMode) {
            /* 开启字段全大写申明 */
            column = column.toUpperCase();
        }
        return column;
    }

    public TtypeEnum getExactTtype() {
        return TtypeEnum.RELATED.equals(this.getTtype()) ? this.getRelatedTtype() : this.getTtype();
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "modelDefinition", "selection", "modelName",
                "modelReferences", "modelThrough", "sequenceConfig");
    }

    public static String sign(String model, String field) {
        return model + CharacterConstants.SEPARATOR_DOT + field;
    }

}
