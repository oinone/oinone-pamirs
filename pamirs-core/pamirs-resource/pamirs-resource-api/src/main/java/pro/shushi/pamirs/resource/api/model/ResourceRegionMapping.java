package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.model(ResourceRegionMapping.MODEL_MODEL)
@Model.Advanced(name = "ResourceRegionMapping", unique = "model,relationCode,keywords")
@Model(displayName = "地区关键字映射", labelFields = "keywords")
public class ResourceRegionMapping extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceRegionMapping";

    @Field.String
    @Field(displayName = "关联模型", required = true, summary = "区分被映射的是省/市/区")
    private String model;

    @Field.String
    @Field(displayName = "关联编码", required = true, summary = "被映射地区的编码")
    private String relationCode;

    @Field.String
    @Field(displayName = "映射关键字", required = true)
    private String keywords;
}
