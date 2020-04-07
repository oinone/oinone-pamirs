package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.enumclass.InheritedTypeEnumCls;

/**
 * 模型继承
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.ModelInherited")
@Model(displayName = "模型继承", summary = "模型继承", pk = {"model","superModel"}, labelFields = {"model","superModel"})
public class ModelInherited extends BaseModel {

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model")
    @Field(displayName = "模型")
    private ModelDefinition modelDefinition;

    @Base
    @Field(invisible = true)
    private String model;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "superModel", referenceFields = "model")
    @Field(displayName = "父模型")
    private ModelDefinition superModelDefinition;

    @Base
    @Field(invisible = true)
    private String superModel;

    @Base
    @Field.Enum
    @Field(displayName = "继承方式")
    private InheritedTypeEnumCls type;

}