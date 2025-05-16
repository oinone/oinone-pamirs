package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

import java.util.List;

@Model.model(ResourceCountryGroup.MODEL_MODEL)
@Model.Advanced(name = "ResourceCountryGroup", unique = {"code"})
@Model(displayName = "国家分组/洲", labelFields = "name", summary = "默认提供五大洲的国家分组，且不可见，不可删除，不可编辑")
public class ResourceCountryGroup extends CodeModel {

    public static final String MODEL_MODEL = "resource.ResourceCountryGroup";

    @Field.String
    @Field(displayName = "名称", required = true)
    private String name;

    @Field.many2many(through = "ResourceCountryGroupCountryRel")
    @Field(displayName = "国家列表")
    private List<ResourceCountry> countryList;
}
