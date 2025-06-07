package pro.shushi.pamirs.international.tmodel;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.international.util.InternationalInfo;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.TimeZoneTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceCountry;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.user.api.model.PamirsUser;

@Model.model("resource.InternationalTransient")
@Model(displayName = "国际化模型")
public class InternationalTransient extends TransientModel {

    @Field.many2one
    @Field(displayName = "货币配置")
    private I18NCurrencyConfig currencyConfig;

    @Field.many2one
    @Field(displayName = "日期配置")
    private I18NDateConfig dateConfig;

    @Field.many2one
    @Field(displayName = "整数配置")
    private I18NIntegerConfig integerConfig;

    @Field.many2one
    @Field(displayName = "浮点数配置")
    private I18NFloatConfig floatConfig;

    @Field.many2one
    @Field(displayName = "字符串配置")
    private I18NStringConfig stringConfig;

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    public InternationalTransient construct(InternationalTransient data) {

        PamirsUser user = null;
        if (null != PamirsSession.getUserId()) {
            user = new PamirsUser()
                    .setId(PamirsSession.getUserId()).<PamirsUser>queryById()
                    .fieldQuery(PamirsUser::getLang)
                    .fieldQuery(PamirsUser::getCountry)
                    .fieldQuery(PamirsUser::getCurrency);
        }

        ResourceLang lang = null;
        ResourceCurrency currency;
        ResourceCountry country = null;
        String timeZoneType = "";
        String baseTimeZoneType;
        if (checkUserIsNull(user)) {
            lang = InternationalInfo.getDefaultLang();
            currency = InternationalInfo.getDefaultCurrency();
            timeZoneType = lang.getTimezoneType().value();
        } else {
            lang = user.getLang();
            currency = user.getCurrency();
            country = user.getCountry();
            if (null != user.getTimeZoneType()) {
                timeZoneType = user.getTimeZoneType().value();
            }
            if (StringUtils.isBlank(timeZoneType)) {
                timeZoneType = lang.getTimezoneType().value();
            }
        }
        baseTimeZoneType = InternationalInfo.getDefaultBaseTimeZone();
        if (country == null) {
            country = InternationalInfo.getDefaultCountry();
        }
        if (StringUtils.isBlank(timeZoneType)) {
            timeZoneType = InternationalInfo.getDefaultTimeZone();
        }

        I18NCurrencyConfig iCurrencyConfig = new I18NCurrencyConfig()
                .setSymbol(currency.getSymbol())
                .setCurrencySubunitLabel(currency.getCurrencySubunitLabel())
                .setCurrencyUnitLabel(currency.getCurrencyUnitLabel())
                .setDecimalPlaces(currency.getDecimalPlaces())
                .setDecimalPoint(lang.getDecimalPoint())
                .setPosition(currency.getPosition())
                .setDirection(lang.getDirection())
                .setGroupingRule(lang.getGroupingRule())
                .setThousandsSep(lang.getThousandsSep());

        I18NDateConfig iDateConfig = new I18NDateConfig()
                .setDateFormat(lang.getDateFormat())
                .setDirection(lang.getDirection())
                .setTimeFormat(lang.getTimeFormat())
                .setWeekStart(lang.getWeekStart())
                .setBaseTimezoneType(TimeZoneTypeEnum.getEnumByValue(baseTimeZoneType))
                .setTimeZoneType(TimeZoneTypeEnum.getEnumByValue(timeZoneType));

        I18NIntegerConfig iIntegerConfig = new I18NIntegerConfig()
                .setDirection(lang.getDirection())
                .setGroupingRule(lang.getGroupingRule())
                .setThousandsSep(lang.getThousandsSep());

        I18NFloatConfig iFloatConfig = new I18NFloatConfig()
                .setDecimalPoint(lang.getDecimalPoint())
                .setDirection(lang.getDirection())
                .setDecimalPlaces(currency.getDecimalPlaces())
                .setGroupingRule(lang.getGroupingRule())
                .setThousandsSep(lang.getThousandsSep());

        I18NStringConfig iStringConfig = new I18NStringConfig()
                .setDirection(lang.getDirection())
                .setPhoneCode(country.getPhoneCode())
                .setAddrFormat(country.getAddrFormat())
                .setNamePosition(country.getNamePosition());

        return data.setCurrencyConfig(iCurrencyConfig)
                .setDateConfig(iDateConfig)
                .setFloatConfig(iFloatConfig)
                .setIntegerConfig(iIntegerConfig).setStringConfig(iStringConfig);
    }

    private static Boolean checkUserIsNull(PamirsUser user) {
        if (null == user) {
            return Boolean.TRUE;
        }
        if (null == user.getCountry() || null == user.getCountry().getId()) {
            return Boolean.TRUE;
        }
        if (null == user.getCurrency() || null == user.getCurrency().getId()) {
            return Boolean.TRUE;
        }
        if (null == user.getLang() || null == user.getLang().getId()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
