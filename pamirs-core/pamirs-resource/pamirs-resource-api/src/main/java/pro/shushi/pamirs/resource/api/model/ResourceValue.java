package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(ResourceValue.MODEL_MODEL)
@Model(displayName = "资源键值")
public class ResourceValue extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceValue";

    @Field.String
    @Field(displayName = "键", required = true)
    private String key;

    @Field.String
    @Field(displayName = "值", required = true)
    private String value;
}
