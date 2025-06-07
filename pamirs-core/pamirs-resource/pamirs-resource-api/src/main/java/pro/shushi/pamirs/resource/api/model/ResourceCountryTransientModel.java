package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(ResourceCountryTransientModel.MODEL_MODEL)
@Model(displayName = "通用国家临时模型")
public class ResourceCountryTransientModel extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceCountryTransientModel";

    // FIXME: zbh 20210825 What's that?
    @Field.many2one
    @Field(displayName = "国家OnlyId")
    private ResourceCountry countryOnlyId;

    @Field.many2one
    @Field(displayName = "国家")
    private ResourceCountry country;

}
