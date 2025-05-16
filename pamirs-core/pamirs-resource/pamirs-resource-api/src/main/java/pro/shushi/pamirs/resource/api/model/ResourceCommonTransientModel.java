package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(ResourceCommonTransientModel.MODEL_MODEL)
@Model(displayName = "通用配置")
public class ResourceCommonTransientModel extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceCommonTransientModel";

    //    @Field.String
    @Field.many2one
    @Field(displayName = "语言")
    private ResourceLangTransientModel lang;

    @Field.many2one
    @Field(displayName = "国家")
    private ResourceCountryTransientModel country;

    @Field.many2one
    @Field(displayName = "汇率")
    private ResourceCurrencyRateTransientModel rate;

    @Field.many2one
    @Field(displayName = "货币")
    private ResourceCurrencyTransientModel currency;


//    @Action(displayName = "保存通用配置", bindingType = ViewTypeEnum.FORM)
//    public ResourceCommonTransientModel saveCommon(ResourceCommonTransientModel common) {
//        Optional.ofNullable(common.getLang()).ifPresent(_langT -> {
//            Optional.ofNullable(_langT.getLang()).ifPresent(_lang -> {
//                new ResourceLang().update(_lang);
//            });
//        });
//
//        Optional.ofNullable(common.getCountry()).ifPresent(_countryT -> {
//            Optional.ofNullable(_countryT.getCountry()).ifPresent(_country -> {
//                new ResourceCountry().update(_country);
//            });
//        });
//
//        Optional.ofNullable(common.getRate()).ifPresent(_rateT -> {
//            Optional.ofNullable(_rateT.getRate()).ifPresent(_rate -> {
//                new ResourceAutoCurrencyRateConfig().update(_rate);
//            });
//        });
//
//        Optional.ofNullable(common.getCurrency()).ifPresent(_currencyT -> {
//            Optional.ofNullable(_currencyT.getCurrency()).ifPresent(_currency -> {
//                new ResourceCurrency().update(_currency);
//            });
//        });
//        return common;
//    }

}
