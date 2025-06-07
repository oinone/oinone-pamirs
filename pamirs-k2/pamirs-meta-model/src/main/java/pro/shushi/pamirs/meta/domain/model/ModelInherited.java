package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.InheritedTypeEnum;

/**
 * 模型继承
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@Base
@Model.Advanced(priority = 8)
@Model.model(ModelInherited.MODEL_MODEL)
@Model(displayName = "模型继承", summary = "模型继承", labelFields = {"model", "superModel"})
public class ModelInherited extends IdModel {

    private static final long serialVersionUID = -1572996333524827246L;

    public static final String MODEL_MODEL = "base.ModelInherited";

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
    private InheritedTypeEnum type;

}