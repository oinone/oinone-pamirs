package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(ResourceCurrencyTransientModel.MODEL_MODEL)
@Model(displayName = "通用货币临时模型")
public class ResourceCurrencyTransientModel extends TransientModel {

    public static final String MODEL_MODEL = "resource.resourceCurrencyTransientModel";

    // FIXME: zbh 20210825 what's that?
    @Field.many2one
    @Field(displayName = "货币OnlyId")
    private ResourceCurrency currencyOnlyId;

    @Field.many2one
    @Field(displayName = "货币")
    private ResourceCurrency currency;


}
