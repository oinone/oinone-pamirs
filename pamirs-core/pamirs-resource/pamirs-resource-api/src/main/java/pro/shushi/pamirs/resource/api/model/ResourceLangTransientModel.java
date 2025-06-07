package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(ResourceLangTransientModel.MODEL_MODEL)
@Model(displayName = "通用语言临时模型")
public class ResourceLangTransientModel extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceLangTransientModel";

    @Field.many2one
    @Field(displayName = "语言")
    private ResourceLang lang;

    // FIXME: zbh 20210825 What's that?
    @Field.many2one
    @Field(displayName = "语言OnlyId")
    private ResourceLang langOnlyId;

}
