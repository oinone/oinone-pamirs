package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enumclass.OnCascadeEnumCls;
import pro.shushi.pamirs.meta.enumclass.RtypeEnumCls;
import pro.shushi.pamirs.meta.enumclass.TtypeEnumCls;

import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;

/**
 * 关系定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@MetaModel(priority = 6)
@Model.Advanced(name = "field", unique = {"name,model"})
@Model.model("base.Relation")
@Model(displayName = "关系", summary = "关系")
public class Relation extends IdModel {

    @Base
    @Field.String
    @Field(displayName = "技术名称", check = "checkFieldName", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "字段编码", summary = "字段编码", check = "checkFieldName", required = true, unique = true, immutable = true)
    private String field;

    @Base
    @Field.Enum
    @Field(required = true, displayName = "关系类型", summary = "关系类型")
    private RtypeEnumCls ttype;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model")
    @Field(displayName = "模型")
    private ModelDefinition modelDefinition;

    @Base
    @Field(displayName = "模型编码", check = "checkModelModel", required = true)
    private String model;

    @Base
    @Field.Related(related = {"modelDefinition","name"})
    @Field(displayName = "模型技术名称", check = "checkFieldName", required = true, store = NullableBoolEnum.TRUE)
    private String modelName;

    @Base
    @Field.Boolean
    @Field.Advanced()
    @Field(displayName = "关系存储", summary = "持久化保存关联关系", defaultValue = "false"/*compute = "defaultRelationStore"*/)
    private Boolean relationStore;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "relationFields"}, referenceFields = {"model", "field"})
    @Field(displayName = "关系字段列表", summary = "自身模型的关系字段")
    private List<ModelField> relationFieldList;

    @Base
    @Field(displayName = "关系字段", summary = "自身模型的关联字段", check = "checkFieldName", invisible = true, serialize = COMMA)
    private List<String> relationFields;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "references", referenceFields = "model")
    @Field(displayName = "关联模型")
    private ModelDefinition modelReferences;

    @Base
    @Field(displayName = "关联模型", check = "checkModelModel", invisible = true)
    private String references;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "through", referenceFields = "model")
    @Field(displayName = "中间模型")
    private ModelDefinition modelThrough;

    @Base
    @Field(displayName = "中间模型", check = "checkModelModel", invisible = true)
    private String through;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"references", "referenceFields"}, referenceFields = {"model", "field"})
    @Field(displayName = "关联字段列表", summary = "关联模型的关联字段")
    private List<ModelField> referenceFieldList;

    @Base
    @Field(displayName = "关联字段", summary = "关联模型的关联字段技术名称", check = "checkFieldName", invisible = true, serialize = COMMA)
    private List<String> referenceFields;

    @Base
    @Field.Integer
    @Field(displayName = "分页数量限制", summary = "查询分页数量", defaultValue = "20")
    private Integer pageSize;

    @Base
    @Field.Integer
    @Field(displayName = "选项数量限制", summary = "模型筛选结果数量限制", defaultValue = "100")
    private Integer domainSize;

    @Base
    @Field.String(size = 1024)
    @Field(displayName = "模型筛选")
    private String domain;

    @Base
    @Field.Enum
    @Field(displayName = "关联更新", defaultValue = "set_null")
    private OnCascadeEnumCls onUpdate;

    @Base
    @Field.Enum
    @Field(displayName = "关联删除", defaultValue = "set_null")
    private OnCascadeEnumCls onDelete;

    @Function
    public ModelField defaultRelationStore(ModelField modelField){
        modelField.setRelationStore(Optional.ofNullable(modelField).map(v->v.getRelationStore())
                .orElse(TtypeEnumCls.isRelationType(modelField.getTtype())));
        return modelField;
    }

}
