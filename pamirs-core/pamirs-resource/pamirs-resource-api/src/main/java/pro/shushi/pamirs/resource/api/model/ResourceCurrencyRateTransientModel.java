package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(ResourceCurrencyRateTransientModel.MODEL_MODEL)
@Model(displayName = "通用汇率临时模型")
public class ResourceCurrencyRateTransientModel extends TransientModel {

    public static final String MODEL_MODEL = "resource.resourceCurrencyRateTransientModel";

    // FIXME: zbh 20210825 What's that?
    @Field.many2one
    @Field(displayName = "汇率OnlyId")
    private ResourceAutoCurrencyRateConfig rateOnlyId;

    @Field.many2one
    @Field(displayName = "汇率")
    private ResourceAutoCurrencyRateConfig rate;

}
