package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import java.util.Optional;

@Slf4j
@Model.model(ResourceGeneral.MODEL_MODEL)
@Model.Advanced(name = "resourceGeneral")
@Model(displayName = "通用")
public class ResourceGeneral extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceGeneral";

    @Field.many2one
    @Field(displayName = "语言")
    private ResourceLang lang;

    @Field.many2one
    @Field(displayName = "基准货币")
    private ResourceCurrency currency;

    @Field.many2one
    @Field(displayName = "汇率")
    private ResourceAutoCurrencyRateConfig currencyRateConfig;

    @Field.many2one
    @Field(displayName = "国家")
    private ResourceCountry country;

    @Function
    public ResourceGeneral construct(ResourceGeneral resourceGeneral) throws Exception {

        ResourceCountry country = resourceGeneral.getCountry();
        ResourceCurrency currency = resourceGeneral.getCurrency();
        ResourceLang lang = resourceGeneral.getLang();
        ResourceAutoCurrencyRateConfig currencyRateConfig = resourceGeneral.getCurrencyRateConfig();

        country = findDataInDb(country);
        if (null == country) {
            ResourceCountry resourceCountry = new ResourceCountry();
            resourceCountry.setCode(DefaultResourceConstants.COUNTRY.getCode());
            country = Optional.ofNullable(findDataInDb(resourceCountry))
                    .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_RESOURCE_COUNTRY).errThrow());
        }

        lang = findDataInDb(lang);
        if (null == lang) {
            lang = Optional.ofNullable(country.getLang())
                    .map(_userLang -> findDataInDb(_userLang))
                    .orElseGet(() -> {
                        ResourceLang defaultlang = new ResourceLang();
                        defaultlang.setActive(ActiveEnum.ACTIVE);
                        defaultlang.setInstallState(true);
                        return Optional.ofNullable(findDataInDb(defaultlang))
                                .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_RESOURCE_LANG).errThrow());
                    });
        }

        currency = findDataInDb(currency);
        if (null == currency) {
            currency = Optional.ofNullable(country.getCurrency())
                    .map(_currency -> findDataInDb(_currency))
                    .orElseGet(() -> {
                        ResourceCurrency defaultCurrency = new ResourceCurrency();
                        defaultCurrency.setActive(true);
                        return Optional.ofNullable(findDataInDb(defaultCurrency))
                                .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_RESOURCE_CURRENCY).errThrow());
                    });
        }
        currencyRateConfig = findDataInDb(currencyRateConfig);
        if (null == currencyRateConfig) {
            currencyRateConfig = new ResourceAutoCurrencyRateConfig();
            currencyRateConfig.setEnable(true);
            currencyRateConfig = Optional.ofNullable(findDataInDb(currencyRateConfig))
                    .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_RESOURCE_AUTO_CURRENCY_CONFIG_ERROR).errThrow());
        }
        return resourceGeneral.setCountry(country)
                .setCurrency(currency)
                .setCurrencyRateConfig(currencyRateConfig)
                .setLang(lang);

    }

    @Action
    public ResourceGeneral update(ResourceGeneral resourceGeneral) throws Exception {

        ResourceCountry country = resourceGeneral.getCountry();
        ResourceCurrency currency = resourceGeneral.getCurrency();
        ResourceAutoCurrencyRateConfig currencyRateConf = resourceGeneral.getCurrencyRateConfig();
        ResourceLang lang = resourceGeneral.getLang();

        country = doUpdata(country);
        currency = doUpdata(currency);
        lang = doUpdata(lang);

        if (null != currencyRateConf && null != currencyRateConf.getEnable() && currencyRateConf.getEnable()) {
            ResourceAutoCurrencyRateConfig resourceAutoCurrencyRateConfig = new ResourceAutoCurrencyRateConfig();
            resourceAutoCurrencyRateConfig.setEnable(true);
            ResourceAutoCurrencyRateConfig dataInDb = findDataInDb(resourceAutoCurrencyRateConfig);
            if (null != dataInDb.getCurrencyService() && dataInDb.getCurrencyService() != resourceAutoCurrencyRateConfig.getCurrencyService()) {
                dataInDb.setEnable(false);
                doUpdata(dataInDb);
            }
        }
        currencyRateConf = doUpdata(currencyRateConf);

        return resourceGeneral.setLang(lang)
                .setCurrency(currency)
                .setCountry(country)
                .setCurrencyRateConfig(currencyRateConf);
    }

    private <T extends IdModel> T doUpdata(T resource) {
        if (null != resource) {
            resource.updateById();
        }
        return resource;
    }

    private <T extends BaseModel> T findDataInDb(T resource) {
        //fixme base 查询数据库
        return resource;
    }
}
