package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.model(ResourceName.MODEL_MODEL)
@Model(labelFields = "name")
public class ResourceName extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceName";

    @Field.String
    @Field(displayName = "姓")
    private String firstName;

    @Field.String
    @Field(displayName = "中间名")
    private String midName;

    @Field.String
    @Field(displayName = "名")
    private String lastName;

}
