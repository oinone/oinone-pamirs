package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

@Base
@Model.Advanced(name = "modelRelation")
@Model.model("base.ModelRelation")
@Model(displayName = "关系表", summary = "关系表")
public class ModelRelation extends IdModel {

    private static final long serialVersionUID = -4404875782577859749L;

    @Base
    @Field.many2one
    private ModuleDefinition module;

    @Base
    @Field.many2one
    private ModelDefinition model;

    @Base
    @Field(displayName = "关系名称")
    @Field.String
    private String name;

}
