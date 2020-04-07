package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.fun.CheckExpression;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enumclass.DataSourceEnumCls;
import pro.shushi.pamirs.meta.enumclass.ModelTypeEnumCls;
import pro.shushi.pamirs.meta.enumclass.SystemSourceEnumCls;

import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;

/**
 * 模型定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
@MetaModel(priority = 4, core = true)
@Base
@Model.model("base.Model")
@Model(displayName = "模型定义", summary = "模型定义", labelFields = "displayName")
public class ModelDefinition extends IdModel {

    @Base
    @Field.one2many(limit = 3)
    @Field.Relation(relationFields = {"model", "pk"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "主键字段列表", required = true)
    private List<ModelField> pkList;

    @Base
    @Field(displayName = "主键", check = "checkFieldName", required = true, serialize = COMMA)
    private List<String> pk;

    @Base
    @Field.String
    @Field(displayName = "模型编码", check = "checkModelModel", required = true, unique = true, immutable = true)
    private String model;

    @Base
    @Field.String
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "前端技术名称", check = "checkModelName", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "模型代码名称", check = "checkModelName", required = true)
    private String lname;

    @Base
    @Field(displayName = "分片名称")
    private String shardingName;

    @Base
    @Field(displayName = "数据库名称")
    private String database;

    @Base
    @Field.String
    @Field(displayName = "数据表名称", check = "checkTableName", required = true, unique = true)
    private String table;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "model")
    @Field(displayName = "字段")
    private List<ModelField> modelFields;

    @Base
    @Field.Enum
    @Field(displayName = "模型类型", defaultValue = "store")
    private ModelTypeEnumCls type;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "proxy")
    @Field(displayName = "代理模型")
    private ModelDefinition proxyModel;

    @Base
    @Field(displayName = "代理模型编码", check = "checkModelModel", required = true)
    private String proxy;

    @Base
    @Field.Boolean
    @Field(displayName = "关系模型", defaultValue = "false")
    private Boolean isRelationship;

    @Base
    @Field.String
    @Field(displayName = "描述摘要")
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述")
    private String description;

    @Base
    @Field.Enum
    @Field(displayName = "来源")
    private SystemSourceEnumCls source;

    @Base
    @Field.Integer
    @Field(displayName = "排序", defaultValue = "100")
    private Long priority;

    @Base
    @Field.Boolean
    @Field(displayName = "可管理", summary = "是否允许系统根据模型变化自动创建表和更新表", defaultValue = "true")
    private Boolean managed;

    @Base
    @Field.String
    @Field(displayName = "排序", defaultValue = "priority desc")
    private String ordering;

    @Base
    @Field.Enum
    @Field(displayName = "数据源类型", defaultValue = "MYSQL", invisible = true)
    private DataSourceEnumCls ds;

    @Base
    @Field.one2one
    @Field.Relation(store = false)
    @Field(summary = "编码生成配置", displayName = "编码生成配置", store = NullableBoolEnum.TRUE)
    private SequenceConfig sequenceConfig;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "model")
    @Field(displayName = "父模型列表", summary = "父模型列表")
    private List<ModelInherited> superModelList;

    @Base
    @Field.Related(related = {"superModelList","superModel"})
    @Field(displayName = "父模型", summary = "父模型", check = "checkModelModel", serialize = COMMA, invisible = true)
    private List<String> superModels;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "model", referenceFields = "superModel")
    @Field(displayName = "子模型列表", summary = "子模型列表")
    private List<ModelInherited> subModelList;

    @Base
    @Field.Related(related = {"subModelList","model"})
    @Field(displayName = "子模型", summary = "子模型", check = "checkModelModel", serialize = COMMA, invisible = true)
    private List<String> subModels;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "unInheritedFields"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "不继承字段列表")
    private List<ModelField> unInheritedFieldList;

    @Base
    @Field(displayName = "不继承字段", check = "checkFieldName", multi = true, serialize = COMMA)
    private List<String> unInheritedFields;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "unInheritedFunctions"}, referenceFields = {"namespace", "fun"}, domain = "model=eq=@{model}")
    @Field(displayName = "不继承字段函数")
    private List<FunctionDefinition> unInheritedFunctionList;

    @Base
    @Field(displayName = "不继承函数", check = "checkFieldName", multi = true, serialize = COMMA)
    private List<String> unInheritedFunctions;

    @Base
    @Field.one2many
    @Field.Relation(store = false, domain = "namespace=eq=constraint,scene=contains=constraint")
    @Field(displayName = "校验函数列表")
    private List<FunctionDefinition> checkList;

    @Base
    @Field(displayName = "校验函数", multi = true, serialize = COMMA)
    private List<String> checks;

    @Base
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "校验表达式")
    private List<CheckExpression> ruleList;

    @Base
    @Field.Related(related = {"ruleList","rule"})
    @Field(displayName = "校验表达式", check = "checkExpression", store = NullableBoolEnum.TRUE, serialize = COMMA)
    private List<String> rules;

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
    @Field(displayName = "唯一索引", check = "checkFieldName", multi = true, serialize = COMMA)
    private List<String> uniques;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "indexes"}, referenceFields = {"model", "fields"}, domain = "model=eq=@{model},unique=eq=false")
    @Field(displayName = "索引")
    private List<ModelIndex> indexList;

    @Base
    @Field(displayName = "索引", check = "checkFieldName", multi = true, serialize = COMMA)
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
    @Field(invisible = true)
    private Boolean optimisticLocker;

    @Base
    @Field(invisible = true)
    private String optimisticLockerColumn;

    @Function
    public ModelDefinition construct(ModelDefinition model){
        model.setTable(Optional.ofNullable(model).map(v->v.getTable()).orElse(PStringUtils.fieldName2Column(model.getName())));
        return model;
    }

}
