package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;

/**
 * 模型索引
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Base
@Model.Advanced(unique = {"model, fields"})
@Model.model("base.ModelIndex")
@Model(displayName = "模型索引", summary = "模型索引", labelFields = {"model","fields"})
public class ModelIndex extends IdModel {

    @Base
    @Field.Boolean
    @Field(displayName = "唯一索引")
    private Boolean unique;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model")
    @Field(displayName = "模型定义")
    private ModelDefinition modelDefinition;

    @Base
    @Field(displayName = "模型")
    private String model;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "fields"}, referenceFields = {"model", "name"}, domain = "model=eq=@{model}")
    @Field(displayName = "索引字段列表")
    private List<ModelField> fieldList;

    @Base
    @Field.String
    @Field(displayName = "索引字段", multi = true, serialize = COMMA)
    private List<String> fields;

}