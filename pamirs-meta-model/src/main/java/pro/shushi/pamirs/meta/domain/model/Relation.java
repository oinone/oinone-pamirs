package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.*;

import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;
import static pro.shushi.pamirs.meta.annotation.Field.serialize.JSON;
import static pro.shushi.pamirs.meta.domain.model.Relation.MODEL_MODEL;

/**
 * 关系定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@Base
@MetaModel(priority = 6)
@Model.Advanced(table = "base_field", remark = "字段", priority = 9, unique = {"model,name", "model,field"})
@Model.model(MODEL_MODEL)
@Model(displayName = "关系", summary = "关系")
public class Relation extends MetaBaseModel implements MetaCheckConstants {

    private static final long serialVersionUID = -2529179591615005845L;

    public static final String MODEL_MODEL = "base.Relation";

    @Base
    @Field(displayName = "位", invisible = true, defaultValue = "0")
    private Long bitOptions;// 勿修改此属性，因为继承晚于元模型计算

    @Base
    @Field.Advanced(columnDefinition = "TINYINT(1) NOT NULL DEFAULT '1'")
    @Field(displayName = "系统元数据", defaultValue = "true", summary = "由系统产生的元数据")
    private Boolean sys;// 勿修改此属性，因为继承晚于元模型计算

    @Base
    @Field(displayName = "系统来源", index = true, defaultValue = "MANUAL", summary = "BASE是系统创建, MANUAL是人工创建", translate = true)
    private SystemSourceEnum systemSource;// 勿修改此属性，因为继承晚于元模型计算

    @Base
    @Validation(check = checkFieldName)
    @Field.String
    @Field(displayName = "api名称", required = true)
    private String name;

    @Base
    @Validation(check = checkFieldField)
    @Field.String
    @Field(displayName = "字段编码", summary = "字段编码", required = true)
    private String field;

    @Base
    @Field.Enum
    @Field(required = true, displayName = "关系类型", summary = "关系类型")
    private RtypeEnum ttype;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model")
    @Field(displayName = "模型")
    private ModelDefinition modelDefinition;

    @Base
    @Validation(check = checkModelModel)
    @Field(displayName = "模型编码", required = true)
    private String model;

    @Base
    @Field.Related(related = {"modelDefinition", "name"})
    @Field(displayName = "模型api名称")
    private String modelName;

    @Base
    @Field.Boolean
    @Field.Advanced()
    @Field(displayName = "关系存储", summary = "持久化保存关联关系", defaultValue = "true")
    private Boolean relationStore;

    @Base
    @Field.Boolean
    @Field(displayName = "反向关联", summary = "反向关联，将一对多关系存储在关系模型", defaultValue = "false")
    private Boolean inverse;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "relationFields"}, referenceFields = {"model", "field"})
    @Field(displayName = "关系字段列表", summary = "自身模型的关系字段")
    private List<ModelField> relationFieldList;

    @Base
    @Validation(check = checkFieldConfig)
    @Field.String(size = 512)
    @Field(displayName = "关系字段", summary = "自身模型的关联字段编码", invisible = true, serialize = COMMA)
    private List<String> relationFields;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "references", referenceFields = "model")
    @Field(displayName = "关联模型")
    private ModelDefinition modelReferences;

    @Base
    @Validation(check = checkModelModel)
    @Field(displayName = "关联模型编码", invisible = true)
    private String references;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "through", referenceFields = "model")
    @Field(displayName = "中间模型")
    private ModelDefinition modelThrough;

    @Base
    @Validation(check = checkModelModel)
    @Field(displayName = "中间模型编码", invisible = true)
    private String through;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"references", "referenceFields"}, referenceFields = {"model", "field"})
    @Field(displayName = "关联字段列表", summary = "关联模型的关联字段")
    private List<ModelField> referenceFieldList;

    @Base
    @Validation(check = checkFieldName)
    @Field.String(size = 512)
    @Field(displayName = "关联字段", summary = "关联模型的关联字段编码", invisible = true, serialize = COMMA)
    private List<String> referenceFields;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "throughRelationFields"}, referenceFields = {"model", "field"})
    @Field(displayName = "中间模型关系字段列表", summary = "中间模型的关系字段")
    private List<ModelField> throughRelationFieldList;

    @Base
    @Validation(check = checkFieldName)
    @Field(displayName = "中间模型关系字段", summary = "中间模型的关联字段", invisible = true, serialize = COMMA)
    private List<String> throughRelationFields;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"references", "throughReferenceFields"}, referenceFields = {"model", "field"})
    @Field(displayName = "中间模型关联字段列表", summary = "中间模型的关联模型的关联字段")
    private List<ModelField> throughReferenceFieldList;

    @Base
    @Validation(check = checkFieldName)
    @Field(displayName = "中间模型关联字段", summary = "中间模型的关联模型的关联字段编码", invisible = true, serialize = COMMA)
    private List<String> throughReferenceFields;

    @Base
    @Field.Integer
    @Field(displayName = "分页数量限制", summary = "查询分页数量", defaultValue = "20")
    private Long pageSize;

    @Base
    @Field.String
    @Field(displayName = "排序")
    private String ordering;

    @Base
    @Field.Integer
    @Field(displayName = "选项数量限制", summary = "模型筛选结果数量限制", defaultValue = "15")
    private Integer domainSize;

    @Base
    @Validation(check = checkRsqlExpression)
    @Field.String(size = 1024)
    @Field(displayName = "模型筛选")
    private String domain;

    @Base
    @Field.String(size = 1024)
    @Field(displayName = "关联上下文", serialize = JSON)
    private Map<String, Object> context;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "search"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=@{model},name=eq=@{search}")
    @Field(summary = "搜索函数", displayName = "搜索函数")
    private FunctionDefinition searchFunction;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "搜索函数编码", summary = "搜索函数编码")
    private String search;

    @Base
    @Field.Enum
    @Field(displayName = "关联更新", defaultValue = "set_null")
    private OnCascadeEnum onUpdate;

    @Base
    @Field.Enum
    @Field(displayName = "关联删除", defaultValue = "set_null")
    private OnCascadeEnum onDelete;

    public ModelField defaultRelationStore(ModelField modelField) {
        if (!TtypeEnum.isRelationType(modelField.getTtype())) {
            modelField.setRelationStore(false);
        } else {
            if (null == modelField.getRelationStore() || modelField.getRelationStore()) {
                ModelConfig relationModel      = PamirsSession.getContext().getModelConfig(modelField.getModel());
                ModelConfig referenceModel     = PamirsSession.getContext().getModelConfig(modelField.getReferences());
                String      referenceModelType = ModelTypeEnum.TRANSIENT.value();
                if (null != referenceModel) {
                    referenceModelType = referenceModel.getType().value();
                } else if (null != modelField.getRelationStore()) {
                    throw PamirsException.construct(MetaExpEnumerate.BASE_REFERENCE_MODEL_DEPENDENCY_ERROR)
                            .appendMsg("model:" + modelField.getModel() + ",field:" + modelField.getField()).errThrow();
                }
                modelField.setRelationStore(!ModelTypeEnum.TRANSIENT.value().equals(relationModel.getType().value())
                        && !ModelTypeEnum.TRANSIENT.value().equals(referenceModelType));
            }
        }
        return modelField;
    }

    @Override
    public String getSignModel() {
        return ModelField.MODEL_MODEL;
    }

}
