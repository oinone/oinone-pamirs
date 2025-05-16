package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

@Model.model(ResourceCompanyNature.MODEL_MODEL)
@Model.Advanced(name = "resourceCompanyNature", unique = {"code"})
@Model(displayName = "公司性质", labelFields = "name")
public class ResourceCompanyNature extends CodeModel {

    public static final String MODEL_MODEL = "resource.ResourceCompanyNature";

    @Field.String
    @Field(required = true, displayName = "名称")
    private String name;

    @Field.String(size = 512)
    @Field(displayName = "描述")
    private String description;

}
