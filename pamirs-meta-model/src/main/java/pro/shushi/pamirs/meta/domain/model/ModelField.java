package pro.shushi.pamirs.meta.domain.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.fun.CheckExpression;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FieldTrackEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enumclass.DateFormatEnumCls;
import pro.shushi.pamirs.meta.enumclass.TtypeEnumCls;

import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;

/**
 * 字段定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
@MetaModel(priority = 5, core = true)
@Base
@Model.Advanced(name = "field", unique = {"name,model","column,model"})
@Model.model("base.Field")
@Model(displayName = "模型字段", summary = "模型字段", labelFields = "displayName")
public class ModelField extends Relation {

    @Base
    @Field.String
    @Field(displayName = "字段名称", required = true, translate = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "代码字段名称", summary = "代码字段名称", check = "checkFieldName", invisible = true)
    private String lname;

    @Base
    @Field.String
    @Field(displayName = "数据库字段", summary = "数据库字段", check = "checkColumnName", invisible = true)
    private String column;

    @Base
    @Field.String
    @Field(displayName = "字段备注")
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "字段描述")
    private String description;

    @Base
    @Field.Enum
    @Field(required = true, displayName = "字段类型", summary = "字段的业务类型")
    private TtypeEnumCls ttype;

    @Base
    @Field.Enum
    @Field(required = true, displayName = "引用字段类型", summary = "引用字段的业务类型")
    private TtypeEnumCls relatedTtype;

    @Base
    @Field.String
    @Field(displayName = "字段后台类型", summary = "字段java类型", check = "checkLtype", invisible = true)
    private String ltype;

    @Base
    @Field.String
    @Field(displayName = "字段泛化类型", summary = "字段的java类型的泛型", check = "checkLtypeT", invisible = true)
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
    private Short decimal;

    @Base
    @Field.one2one
    @Field.Relation(store = false)
    @Field(summary = "序列生成配置", displayName = "序列生成配置", store = NullableBoolEnum.TRUE)
    private SequenceConfig sequenceConfig;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"#serialize#", "serialize"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=serialize,ttype=eq=@{argTtypes}")
    @Field(summary = "序列化函数", displayName = "序列化函数")
    private FunctionDefinition serializeFunction;

    @Base
    @Field(summary = "序列化", displayName = "序列化", invisible = true)
    private String serialize;

    @Base
    @Field.Enum
    @Field(summary = "时间格式", displayName = "时间格式")
    private DateFormatEnumCls format;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "related"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "引用字段列表")
    private List<ModelField> relatedList;

    @Base
    @Field.Related(related = {"relatedList","field"})
    @Field(displayName = "引用字段", summary = "引用字段", check = "checkDotExpression", serialize = "DOT")
    private List<String> related;

    @Base
    @Field.Enum
    @Field(displayName = "字段来源", summary = "BASE是系统创建, MANUAL是人工创建")
    private SystemSourceEnum source;

    @Base
    @Field.String
    @Field(displayName = "免权限控制", summary = "免权限控制")
    private List<String> sudo;

    @Base
    @Field.many2one
    @Field.Relation(referenceFields = "dictionary")
    @Field(displayName = "可选项")
    private DataDictionary selection;

    @Base
    @Field(displayName = "数据字典", invisible = true)
    private String dictionary;

    @Base
    @Field.Related(related = {"selection","itemList"})
    @Field(displayName = "可选项", invisible = true)
    private String options;

    @Base
    @Field.Boolean
    @Field(displayName = "存储", summary = "存储字段会持久化保存")
    private Boolean store;

    @Base
    @Field.Integer
    @Field(displayName = "排序", defaultValue = "100")
    private Long priority;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "compute"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=@{model},name=eq=@{compute}")
    @Field(summary = "计算函数", displayName = "计算函数")
    private FunctionDefinition computeFunction;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "计算规则", summary = "计算字段的规则")
    private String compute;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "inverse"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=@{model},name=eq=@{inverse}")
    @Field(summary = "反向计算函数", displayName = "反向计算函数")
    private FunctionDefinition inverseFunction;

    @Base
    @Field.String
    @Field(displayName = "反向计算", summary = "字段值变化反向计算监听字段值并持久化")
    private String inverse;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "search"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=@{model},name=eq=@{search}")
    @Field(summary = "搜索函数", displayName = "搜索函数")
    private FunctionDefinition searchFunction;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "搜索规则", summary = "自定义搜索规则")
    private String search;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "watch"}, referenceFields = {"model", "field"}, domain = "model=eq=@{model}")
    @Field(displayName = "监听字段列表")
    private List<ModelField> watchList;

    @Base
    @Field(displayName = "监听字段", summary = "监听字段", check = "checkFieldName", serialize = COMMA)
    private List<String> watch;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"#constraint#", "checks"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=constraint,ttype=eq=@{argTtypes}")
    @Field(displayName = "校验函数列表")
    private List<FunctionDefinition> checkList;

    @Base
    @Field.String
    @Field(displayName = "校验函数")
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
    @Field(displayName = "只读", defaultValue = "false")
    private Boolean readonly;

    @Base
    @Field.Boolean
    @Field(displayName = "索引", defaultValue = "false")
    private Boolean index;

    @Base
    @Field.Boolean
    @Field(displayName = "是否必须", defaultValue = "false")
    private Boolean required;

    @Base
    @Field.Boolean
    @Field(displayName = "唯一", defaultValue = "false")
    private Boolean unique;

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

    @Function
    public ModelField construct(ModelField modelField){
        if(null == modelField.getStore()){
            modelField.setStore(Optional.ofNullable(modelField).map(v->v.getStore())
                    .orElse(!TtypeEnumCls.isRelationType(modelField.getTtype())));
        }
        if(modelField.getStore() && StringUtils.isBlank(modelField.getColumn())){
            modelField.setColumn(Optional.ofNullable(modelField).map(v->v.getColumn()).orElse(PStringUtils.fieldName2Column(modelField.getName())));
        }
        return modelField;
    }

    public ModelField defaultColumnDefinition(ModelField modelField){
        if(null == modelField.getStore()){
            construct(modelField);
        }
        if(!modelField.getStore()){
            modelField.unsetColumnDefinition();
            return modelField;
        }
        if(StringUtils.isNotBlank(modelField.getColumnDefinition())){
            return modelField;
        }
        TypeProcessor typeProcessor = MetaApiFactory.getApi(TypeProcessor.class);
        String columnDefinition = typeProcessor.defaultColumnTypeFromTtype(modelField.getTtype().value(),
                modelField.getLtype(), modelField.getMulti(), modelField.getSize(), modelField.getDecimal());
        columnDefinition = typeProcessor.defaultColumnDefinition(columnDefinition, !modelField.getRequired(), modelField.getDefaultValue(), null);
        modelField.setColumnDefinition(columnDefinition);
        return modelField;
    }

}
