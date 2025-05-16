package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

import java.math.BigDecimal;

//TODO
@Model.model(ResourceCurrencyRate.MODEL_MODEL)
@Model.Advanced(name = "resourceCurrencyRate")
@Model(displayName = "汇率")
public class ResourceCurrencyRate extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceCurrencyRate";

    @Field.many2one
    @Field(required = true, displayName = "基准货币")
    private ResourceCurrency currency;

    @Field.many2one
    @Field(required = true, displayName = "目标货币")
    private ResourceCurrency toCurrency;

    @Field.Float
    @Field(required = true, displayName = "汇率")
    private BigDecimal rate;

    @Function
    public ResourceCurrencyRate construct(ResourceCurrencyRate rate) {
//        List<ResourceConfig> list = new ResourceConfig().queryList(Pops.<ResourceConfig>lambdaQuery().eq(ResourceConfig::getKey, ResourceConstants.DEFAULT_CURRENCY));
//        if (CollectionUtils.isNotEmpty(list)) {
//            ResourceConfig resourceConfig = list.get(0);
//            String value = resourceConfig.getValue();
//            PageCondition currencyCondition = (PageCondition) new PageCondition(ResourceConfig.class).setWhere(" `key`= " + ResourceConstants.DEFAULT_CURRENCY);
//            List<ResourceCurrency> currencyList = new ResourceCurrency().queryList(Pops.<ResourceCurrency>lambdaQuery().eq(ResourceCurrency::getKey, ResourceConstants.DEFAULT_CURRENCY));
//            if (CollectionUtils.isNotEmpty(currencyList)) {
//                rate.setCurrency(currencyList.get(0));
//                return rate;
//            }
//        }
        return rate;
    }

}
