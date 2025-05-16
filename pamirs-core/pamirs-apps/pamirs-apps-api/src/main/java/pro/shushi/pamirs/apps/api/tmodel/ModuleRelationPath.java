package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

@Base
@Model.model(ModuleRelationPath.MODEL_MODEL)
@Model(displayName = "模块关联-线")
public class ModuleRelationPath extends TransientModel {

    public static final String MODEL_MODEL = "apps.ModuleRelationPath";

    @Field.String
    @Field(displayName = "form")
    private String fromModule;

    @Field.String
    @Field(displayName = "to")
    private String toModule;
}
