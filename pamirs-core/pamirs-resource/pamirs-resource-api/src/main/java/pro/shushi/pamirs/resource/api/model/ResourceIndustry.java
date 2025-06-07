package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

@Model.model(ResourceIndustry.MODEL_MODEL)
@Model.Advanced(name = "resourceIndustry", unique = {"code", "name"})
@Model(displayName = "行业", labelFields = {"name"})
@Model.Code(sequence = "SEQ", prefix = "I", size = 8)
public class ResourceIndustry extends CodeModel {

    public static final String MODEL_MODEL = "resource.ResourceIndustry";

    @Field.String
    @Field(required = true, displayName = "名称")
    private String name;

    @Field.String(size = 512)
    @Field(required = true, displayName = "描述")
    private String description;
}
