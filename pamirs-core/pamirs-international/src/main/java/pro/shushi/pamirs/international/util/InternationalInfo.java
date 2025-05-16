package pro.shushi.pamirs.international.util;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.model.ResourceConfig;
import pro.shushi.pamirs.resource.api.model.ResourceCountry;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;
import pro.shushi.pamirs.resource.api.model.ResourceLang;

import java.util.Optional;

@Slf4j
public class InternationalInfo {

    public static ResourceLang getDefaultLang(){
        return Optional.ofNullable(PamirsSession.getLang())
                .map(_langIsoCode -> new ResourceLang()
                        .setCode(_langIsoCode).<ResourceLang>queryByCode())
                .orElse(Optional.ofNullable(new ResourceConfig().setKey(DefaultResourceConstants.DEFAULT_LANG_KEY).<ResourceConfig>queryOne())
                        .map(ResourceConfig::getValue)
                        .map(_lanCode -> new ResourceLang().setCode(_lanCode).<ResourceLang>queryOne())
                        .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_RESOURCE_LANG).errThrow()));
    }

    public static ResourceCurrency getDefaultCurrency(){
        return Optional.ofNullable(new ResourceConfig().setKey(DefaultResourceConstants.DEFAULT_CURRENCY_KEY).<ResourceConfig>queryOne())
                .map(ResourceConfig::getValue)
                .map(_lanCode -> new ResourceCurrency().setCode(_lanCode).<ResourceCurrency>queryByCode())
                .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_RESOURCE_CURRENCY).errThrow());
    }

    public static String getDefaultTimeZone() {
        return Optional.ofNullable(new ResourceConfig().setKey(DefaultResourceConstants.DEFAULT_TIME_ZONE_KEY).<ResourceConfig>queryOne())
                .map(ResourceConfig::getValue)
                .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_TIME_ZONE_ERROR).errThrow());
    }

    public static String getDefaultBaseTimeZone() {
        return Optional.ofNullable(new ResourceConfig().setKey(DefaultResourceConstants.DEFAULT_TIME_ZONE_KEY).<ResourceConfig>queryOne())
                .map(ResourceConfig::getValue)
                .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_BASE_TIME_ZONE_ERROR).errThrow());
    }

    public static ResourceCountry getDefaultCountry() {
        return Optional.ofNullable(new ResourceConfig().setKey(DefaultResourceConstants.DEFAULT_COUNTRY_KEY).<ResourceConfig>queryOne())
                .map(ResourceConfig::getValue)
                .map(_countryCode -> new ResourceCountry().setCode(_countryCode).<ResourceCountry>queryByCode())
                .orElseThrow(() -> PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_RESOURCE_COUNTRY).errThrow());
    }
}
