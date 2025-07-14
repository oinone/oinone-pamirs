package pro.shushi.pamirs.meta.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.ExpressionConstants;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.constant.NameRegexConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.DiffUtils;
import pro.shushi.pamirs.meta.util.ParserUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;
import static pro.shushi.pamirs.meta.domain.model.ModelDefinition.MODEL_MODEL;

/**
 * 模型定义
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@Slf4j
@MetaModel(priority = 4, core = Class.class)
@Base
@Model.Advanced(name = "modelDefinition", priority = 5, table = ModelDefinition.TABLE_NAME, unique = {"model"})
@Model.model(MODEL_MODEL)
@Model(displayName = "模型", summary = "模型", labelFields = "displayName")
public class ModelDefinition extends MetaBaseModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "base.Model";

    public static final String UE_MODEL_MODEL = "base.UeModel";

    public static final String TABLE_NAME = "base_model";

    private static final long serialVersionUID = -2885889690357763961L;

    @Base
    @Validation(check = checkFieldName)
    @Field(displayName = "数据标题字段", multi = true, serialize = COMMA)
    private List<String> labelFields;

    @Base
    @Field(displayName = "数据标题格式")
    @Field.String(size = 512)
    private String label;

    @Base
    @Field(displayName = "位", invisible = true, defaultValue = "0")
    private Long bitOptions; // 勿修改此属性，因为继承晚于元模型计算

    @Base
    @Field.Advanced(columnDefinition = "TINYINT(1) NOT NULL DEFAULT '1'")
    @Field(displayName = "系统元数据", defaultValue = "true", summary = "由系统产生的元数据")
    private Boolean sys;// 勿修改此属性，因为继承晚于元模型计算

    @Base
    @Field(displayName = "系统来源", defaultValue = "MANUAL", summary = "BASE是系统创建, MANUAL是人工创建")
    private SystemSourceEnum systemSource;// 勿修改此属性，因为继承晚于元模型计算

    @Base
    @Field.one2many(limit = 3, inverse = true)
    @Field.Relation(relationFields = {"model", "pk"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "主键字段列表", required = true)
    private List<ModelField> pkList;

    @Validation(check = checkFieldName)
    @Base
    @Field.String
    @Field(displayName = "主键", multi = true, serialize = COMMA)
    private List<String> pk;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "module")
    @Field(displayName = "所属模块", required = true)
    private ModuleDefinition moduleDefinition;

    @Base
    @Field(displayName = "模块编码", required = true)
    private String module;

    @Base
    @Field(displayName = "数据源模块", required = true)
    private String dsModule;

    @Base
    @Field.Related(related = {"moduleDefinition", "name"})
    @Field
    private String moduleName;

    @Base
    @Validation(check = checkModuleAbbr)
    @Field(displayName = "模块简称", summary = "模块简称仅支持小写字母和数字且不能超过8位字符", required = true)
    private String moduleAbbr;

    @Base
    @Validation(check = checkModelModel)
    @Field.String
    @Field(displayName = "模型编码", required = true, index = true)
    private String model;

    @Base
    @Field.String
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Validation(check = checkModelName)
    @Base
    @Field.String
    @Field(displayName = "api名称", unique = true, required = true)
    private String name;

    @Validation(check = checkModelModel)
    @Base
    @Field.String
    @Field(displayName = "模型代码名称", required = true)
    private String lname;

    @Base
    @Field.String
    @Field(displayName = "低无一体名称", required = true)
    private String fname;

    @Base
    @Validation(check = checkTableName)
    @Field.String
    @Field(displayName = "逻辑数据表名称", index = true)
    private String table;

    @Base
    @Field(displayName = "逻辑数据源名", invisible = true)
    private String dsKey;

    @Base
    @Field.String
    @Field(displayName = "备注", summary = "存储备注")
    private String remark;

    @Base
    @Field.one2many(pageSize = 200)
    @Field.Relation(relationFields = "model")
    @Field(displayName = "字段")
    private List<ModelField> modelFields;

    @Base
    @Field.Enum
    @Field(displayName = "模型类型", defaultValue = "store")
    private ModelTypeEnum type;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "multiTable")
    @Field(displayName = "多表父模型")
    private ModelDefinition multiTableModel;

    @Base
    @Validation(check = checkModelModel)
    @Field(displayName = "多表父模型编码")
    private String multiTable;

    @Base
    @Validation(check = checkFieldName)
    @Field(displayName = "多表类型字段")
    private String multiTableTypeField;

    @Base
    @Field(displayName = "多表类型")
    private String multiTableType;

    @Base
    @Field(displayName = "多表冗余", defaultValue = "false")
    private Boolean redundancy;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "proxy")
    @Field(displayName = "代理模型")
    private ModelDefinition proxyModel;

    @Base
    @Validation(check = checkModelModel)
    @Field(displayName = "代理模型编码")
    private String proxy;

    @Base
    @Field.Boolean
    @Field(displayName = "关系模型", defaultValue = "false")
    private Boolean isRelationship;

    @Base
    @Field.String
    @Field(displayName = "简介", translate = true, summary = "描述摘要")
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述", translate = true, summary = "描述详情")
    private String description;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = MetaDefaultConstants.PRIORITY_VALUE_STRING)
    private Long priority;

    @Base
    @Field.Boolean
    @Field(displayName = "可数据管理", summary = "是否允许系统根据模型变化自动创建表和更新表", defaultValue = "true")
    private Boolean dataManager;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "排序")
    private String ordering;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "sequenceCode", referenceFields = "code")
    @Field(summary = "序列生成配置", displayName = "序列生成配置")
    private SequenceConfig sequenceConfig;

    @Base
    @Field.String(size = 256)
    @Field(displayName = "序列生成配置编码", summary = "序列生成配置编码", index = true)
    private String sequenceCode;

    @Base
    @Field.one2many(inverse = true)
    @Field.Relation(relationFields = "superModels", referenceFields = "model")
    @Field(displayName = "父模型列表", summary = "父模型列表")
    private List<ModelInherited> superModelList;

    @Base
    @Validation(check = checkModelModel)
    @Field.Related(related = {"superModelList", "superModel"})
    @Field(displayName = "父模型", summary = "父模型", serialize = COMMA, invisible = true, store = NullableBoolEnum.TRUE)
    private List<String> superModels;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "unInheritedFields"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "不继承字段列表")
    private List<ModelField> unInheritedFieldList;

    @Base
    @Validation(check = checkFieldName)
    @Field(displayName = "不继承字段", multi = true, serialize = COMMA)
    private List<String> unInheritedFields;

    @Base
    @Field.one2many(inverse = true)
    @Field.Relation(relationFields = {"model", "unInheritedFunctions"}, referenceFields = {"namespace", "fun"}, domain = "model=eq=@{model}")
    @Field(displayName = "不继承字段函数")
    private List<FunctionDefinition> unInheritedFunctionList;

    @Base
    @Validation(check = checkFieldName)
    @Field(displayName = "不继承函数", multi = true, serialize = COMMA)
    private List<String> unInheritedFunctions;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model"}, referenceFields = {"namespace"}, domain = "namespace=eq=@{model}")
    @Field(displayName = "函数列表")
    private List<FunctionDefinition> functions;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "uniques"}, referenceFields = {"model", "fields"}, domain = "model=eq=@{model},unique=eq=true")
    @Field(displayName = "唯一索引列表")
    private List<ModelIndex> uniqueList;

    @Base
    @Validation(check = checkComma)
    @Field(displayName = "唯一索引", multi = true, serialize = COMMA)
    private List<String> uniques;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "indexes"}, referenceFields = {"model", "fields"}, domain = "model=eq=@{model},unique=eq=false")
    @Field(displayName = "索引列表")
    private List<ModelIndex> indexList;

    @Base
    @Validation(check = checkComma)
    @Field(displayName = "索引", multi = true, serialize = COMMA)
    private List<String> indexes;

    @Base
    @Field(invisible = true)
    private Boolean logicDelete;

    @Base
    @Field(invisible = true)
    private String logicDeleteColumn;

    @Base
    @Field(invisible = true)
    private String logicDeleteValue;

    @Base
    @Field(invisible = true)
    private String logicNotDeleteValue;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "optimisticLockerField"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "乐观锁字段")
    private ModelField optimisticLocker;

    @Base
    @Validation(check = checkFieldName)
    @Field.String
    @Field(displayName = "乐观锁", invisible = true)
    private String optimisticLockerField;

    @Base
    @Field.Boolean
    @Field(invisible = true)
    private Boolean underCamel;

    @Base
    @Field.Boolean
    @Field(invisible = true)
    private Boolean capitalMode;

    @Base
    @Field.Enum
    @Field(invisible = true, defaultValue = "utf8mb4")
    private CharsetEnum charset;

    @Base
    @Field.Enum
    @Field(invisible = true, defaultValue = "bin")
    private CollationEnum collate;

    @Base
    @Field(displayName = "分组")
    @Field.many2one
    @Field.Relation(relationFields = "categoryId", referenceFields = "id")
    private ModelCategory category;

    @Base
    @Field(displayName = "分组ID", index = true)
    @Field.Integer
    private Long categoryId;

    @Base
    @Field.Enum
    @Field(displayName = "可见", defaultValue = "true")
    private ActiveEnum show;

    /**
     * 是否静态模型
     */
    @JSONField(serialize = false)
    private boolean staticConfig = false;

    public ModelDefinition setStaticConfig(boolean staticConfig) {
        this.staticConfig = staticConfig;
        return this;
    }

    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    @Function
    public ModelDefinition construct(ModelDefinition data) {
        if (null == data) {
            return null;
        }
        if (null == data.getModule()) {
            return data;
        }
        String modelModel = data.getModel();
        // 计算技术名称
        if (StringUtils.isBlank(data.getName()) && StringUtils.isNotBlank(modelModel)) {
            data.setName(PStringUtils.camelCaseFromModel(modelModel));
        }
        // 计算显示名称
        if (StringUtils.isBlank(data.getDisplayName())) {
            data.setDisplayName(data.getName());
        }
        // 计算简介
        if (StringUtils.isBlank(data.getSummary())) {
            data.setSummary(data.getDisplayName());
        }
        // 计算存储备注
        if (StringUtils.isBlank(data.getRemark())) {
            data.setRemark(data.getSummary());
        }
        // 计算表名
        if (StringUtils.isNotBlank(data.getName()) && StringUtils.isBlank(data.getTable())) {
            if (null == data.getType() || ModelTypeEnum.STORE.equals(data.getType())) {
                if (StringUtils.isBlank(data.getTable())) {
                    data.setTable(generateTable(data));
                }
            } else {
                data.setDataManager(Boolean.FALSE);
            }
        }
        return data;
    }

    public String getCompletedDsKey() {
        return DataPrefixManager.dsPrefix(this.getModule(), this.getModel(), this.getDsKey());
    }

    public ModelField fetchModelField(String field) {
        List<ModelField> modelFieldList = getModelFields();
        if (CollectionUtils.isEmpty(modelFieldList)) {
            return null;
        }
        for (ModelField modelField : modelFieldList) {
            if (field.equals(modelField.getField())) {
                return modelField;
            }
        }
        return null;
    }

    public static String generateTable(ModelDefinition modelDefinition) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelDefinition);
        Boolean underCamel = pamirsTableInfo.getUnderCamel();
        Boolean capitalMode = pamirsTableInfo.getCapitalMode();
        return ModelDefinition.generateTable(modelDefinition, underCamel, capitalMode);
    }

    public static String generateTable(ModelDefinition modelDefinition,
                                       Boolean underCamel, Boolean capitalMode) {
        String originTable = modelDefinition.getTable();
        String table = originTable;
        String name = modelDefinition.getName();
        PamirsMapperConfigurationProxy pamirsMapperConfiguration = CommonApiFactory.getApi(PamirsMapperConfigurationProxy.class);
        boolean isNullOriginTable = StringUtils.isBlank(originTable);
        if (isNullOriginTable) {
            if (null != pamirsMapperConfiguration) {
                table = Optional.ofNullable(pamirsMapperConfiguration.fetchPamirsDataConfiguration(modelDefinition.getCompletedDsKey()))
                        .map(PamirsDataConfiguration::getTablePattern).filter(StringUtils::isNotBlank).orElse(ExpressionConstants.S_PLACEHOLDER_TABLE);
            } else {
                table = name;
            }
        }
        boolean tableNameCaseInsensitive = !Optional.ofNullable(pamirsMapperConfiguration)
                .map(v -> v.fetchPamirsDataConfiguration(modelDefinition.getCompletedDsKey()))
                .map(PamirsDataConfiguration::isTableNameCaseSensitive).orElse(false);
        final String t1 = table;
        if (!isNullOriginTable && NameRegexConstants.ONLY_LETTER_AND_NUMBER_AND_UNDERLINE.matcher(t1).matches()) {
            if (tableNameCaseInsensitive) {
                table = table.toLowerCase();
            }
            return table;
        } else {
            Map<String, Object> context = Optional.ofNullable(pamirsMapperConfiguration)
                    .map(PamirsMapperConfigurationProxy::fetchTableNameComputer).map(v -> v.context(modelDefinition)).orElse(null);
            table = ParserUtil.replaceWithMap(table, context);
            table = String.format(table, name);
        }

        if (underCamel) {
            /* 开启字段下划线申明 */
            table = PStringUtils.fieldName2Column(table);
        }

        if (tableNameCaseInsensitive) {
            table = table.toLowerCase();
        } else if (capitalMode) {
            /* 开启字段全大写申明 */
            table = table.toUpperCase();
        }
        return table;
    }

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "functions", "moduleDefinition", "moduleName",
                "modelFields", "sequenceConfig", "labelFields", "label");
    }

}
