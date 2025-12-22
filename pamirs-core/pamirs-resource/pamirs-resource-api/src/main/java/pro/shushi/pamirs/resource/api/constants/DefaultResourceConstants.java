package pro.shushi.pamirs.resource.api.constants;

import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.resource.api.enmu.*;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceDateFormat;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceTimeFormat;

public interface DefaultResourceConstants {

    String SYSTEM_SOURCE_TYPE = "PAMIRS";

    String ASIA_CODE = "Asia";

    ResourceCountryGroup ASIA = (ResourceCountryGroup) new ResourceCountryGroup().setName("亚洲").setCode(ASIA_CODE);

    String EUROPE_CODE = "Europe";

    ResourceCountryGroup EUROPE = (ResourceCountryGroup) new ResourceCountryGroup().setName("欧洲").setCode(EUROPE_CODE);

    String AMERICAS_CODE = "Americas";

    ResourceCountryGroup AMERICAS = (ResourceCountryGroup) new ResourceCountryGroup().setName("美洲").setCode(AMERICAS_CODE);

    String AFRICA_CODE = "Africa";

    ResourceCountryGroup AFRICA = (ResourceCountryGroup) new ResourceCountryGroup().setName("非洲").setCode(AFRICA_CODE);

    String OCEANIA_CODE = "Oceania";

    ResourceCountryGroup OCEANIA = (ResourceCountryGroup) new ResourceCountryGroup().setName("大洋洲").setCode(OCEANIA_CODE);

    String CURRENCY_CODE = "CNY";

    String COUNTRY_CODE = "CN";

    String COUNTRY_NAME = "中华人民共和国";

    String COUNTRY_SHORT_NAME = "中国";

    // resourceRegion.level
    int REGION_LEVEL_COUNTRY = 1;
    int REGION_LEVEL_PROVINCE = 2;
    int REGION_LEVEL_CITY = 3;
    int REGION_LEVEL_DISTRICT = 4;
    int REGION_LEVEL_STREET = 5;
    String PUBLIC_RESOURCE = "common";
    String PUBLIC_RESOURCE_NAME = "公共资源";

    String CHINESE_PHONE_CODE = "+86";

    String CHINESE_LANGUAGE_CODE = "zh-CN";

    ResourceCountry COUNTRY = (ResourceCountry) new ResourceCountry()
            .setName(COUNTRY_SHORT_NAME)
            .setCompleteName(COUNTRY_NAME)
            .setVatLabel(VatLabelEnum.VAT)
            .setPhoneCode(CHINESE_PHONE_CODE)
            .setCurrencyCode("CNY")
            .setLangCode(CHINESE_LANGUAGE_CODE)
            .setSourceType(ResourceCountry.DEFAULT_SOURCE_TYPE)
            .setAddrFormat("<view widget=\"form\">\n" +
                    "    <config separator=\"-\"/>\n" +
                    "    <field name=\"countryCode\"/>\n" +
                    "    <field name=\"provinceCode\"/>\n" +
                    "    <field name=\"cityCode\"/>\n" +
                    "    <field name=\"districtCode\"/>\n" +
                    "    <field name=\"streetCode\"/>\n" +
                    "    <field name=\"street2\"/>\n" +
                    "</view>")
            .setNamePosition("<view widget=\"form\">\n        <config label-width=\"200\" layout=\"vertical\"/>\n        <group title=\"\" widget=\"fieldset\" cols=\"4\">\n <field name=\"id\" widget=\"Input\" invisible=\"true\" span=\"1\" />\n          <field name=\"firstName\" widget=\"Input\" span=\"1\" />\n          <field name=\"midName\" widget=\"Input\" span=\"1\" />\n          <field name=\"lastName\" widget=\"Input\" span=\"1\" />          \n        </group>\n      </view>")
            .setCountryGroupCode("Asia")
            .setCode(COUNTRY_CODE);

    ResourceRegion COUNTRY_REGION = ResourceCountry.fetchCurrentRegion(COUNTRY);

    ResourceLang CHINESE_LANGUAGE = (ResourceLang) new ResourceLang()
            .setDateFormat("yyyy年MM月dd日")
            .setResourceDateFormat(JsonUtils.parseObject("{\"chinese\":\"YYYY年MM月DD日\",\"chineseMap\":{\"value\":[{\"code\":\"YYYY\",\"concat\":\"年\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]},{\"code\":\"MM\",\"concat\":\"月\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"DD\",\"concat\":\"日\",\"displayName\":\"日\",\"id\":\"D\",\"options\":[{\"code\":\"DD\",\"displayName\":\"01\",\"id\":\"CDD\"},{\"code\":\"D\",\"displayName\":\"1\",\"id\":\"CD\"}]}]},\"chineseYearMonth\":\"YYYY年MM月\",\"chineseYearMonthMap\":{\"value\":[{\"code\":\"YYYY\",\"concat\":\"年\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]},{\"code\":\"MM\",\"concat\":\"月\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]}]},\"hyphen\":\"YYYY-MM-DD\",\"hyphenMap\":{\"value\":[{\"code\":\"YYYY\",\"concat\":\"-\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]},{\"code\":\"MM\",\"concat\":\"-\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"DD\",\"concat\":\"\",\"displayName\":\"日\",\"id\":\"D\",\"options\":[{\"code\":\"DD\",\"displayName\":\"01\",\"id\":\"CDD\"},{\"code\":\"D\",\"displayName\":\"1\",\"id\":\"CD\"}]}]},\"hyphenYearMonth\":\"YYYY-MM\",\"hyphenYearMonthMap\":{\"value\":[{\"code\":\"YYYY\",\"concat\":\"-\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]},{\"code\":\"MM\",\"concat\":\"\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]}]},\"slash\":\"YYYY/MM/DD\",\"slashMap\":{\"value\":[{\"code\":\"YYYY\",\"concat\":\"/\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]},{\"code\":\"MM\",\"concat\":\"/\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"DD\",\"concat\":\"\",\"displayName\":\"日\",\"id\":\"D\",\"options\":[{\"code\":\"DD\",\"displayName\":\"01\",\"id\":\"CDD\"},{\"code\":\"D\",\"displayName\":\"1\",\"id\":\"CD\"}]}]},\"slashYearMonth\":\"YYYY/MM\",\"slashYearMonthMap\":{\"value\":[{\"code\":\"YYYY\",\"concat\":\"/\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]},{\"code\":\"MM\",\"concat\":\"\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]}]}}", ResourceDateFormat.class))
            .setDecimalPoint(".")
            .setDirection(DirectionEnum.LTR)
            .setGroupingRule("3")
            .setIsoCode(CHINESE_LANGUAGE_CODE)
            .setName("简体中文")
            .setThousandsSep(",")
            .setTimeFormat("HH时mm分ss秒")
            .setResourceTimeFormat(JsonUtils.parseObject("{\"apColonNormal\":\"Ahh:mm:ss\",\"apColonNormalMap\":{\"value\":[{\"code\":\"A\",\"concat\":\"\",\"displayName\":\"上午/下午\",\"id\":\"A\",\"options\":[{\"code\":\"A\",\"displayName\":\"上午/下午\",\"id\":\"CA\"}],\"width\":\"80px\"},{\"code\":\"hh\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]}]},\"apColonNormalSss\":\"Ahh:mm:ss.SSS\",\"apColonNormalSssMap\":{\"value\":[{\"code\":\"A\",\"concat\":\"\",\"displayName\":\"上午/下午\",\"id\":\"A\",\"options\":[{\"code\":\"A\",\"displayName\":\"上午/下午\",\"id\":\"CA\"}],\"width\":\"80px\"},{\"code\":\"hh\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]},{\"code\":\"SSS\",\"concat\":\".\",\"displayName\":\"毫秒\",\"id\":\"SS\",\"options\":[{\"code\":\"sss\",\"displayName\":\"001\",\"id\":\"Csss\"}]}]},\"apColonShort\":\"A:hh:mm\",\"apColonShortMap\":{\"value\":[{\"code\":\"A\",\"concat\":\":\",\"displayName\":\"上午/下午\",\"id\":\"A\",\"options\":[{\"code\":\"A\",\"displayName\":\"上午/下午\",\"id\":\"CA\"}],\"width\":\"80px\"},{\"code\":\"hh\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\"\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]}]},\"colonNormal\":\"HH:mm:ss\",\"colonNormalMap\":{\"value\":[{\"code\":\"HH\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]}]},\"colonNormalSss\":\"HH:mm:ss.SSS\",\"colonNormalSssMap\":{\"value\":[{\"code\":\"HH\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]},{\"code\":\"SSS\",\"concat\":\".\",\"displayName\":\"毫秒\",\"id\":\"SS\",\"options\":[{\"code\":\"sss\",\"displayName\":\"001\",\"id\":\"Csss\"}]}]},\"colonShort\":\"HH:mm\",\"colonShortMap\":{\"value\":[{\"code\":\"HH\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\"\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]}]}}", ResourceTimeFormat.class))
            .setWeekStart(WeekStartEnum.MONDAY)
            .setTimezoneType(TimeZoneTypeEnum.Asia_Shanghai)
            .setCalendarType(CalendarTypeEnum.Solar)
            .setInstallState(Boolean.TRUE)
            .setActive(ActiveEnum.ACTIVE)
            .setCode(CHINESE_LANGUAGE_CODE);

    ResourceLang ENGLISH_LANGUAGE = (ResourceLang) new ResourceLang()
            .setDateFormat("MM/dd/yyyy")
            .setResourceDateFormat(JsonUtils.parseObject("{\"chinese\":\"MMMM DD,YYYY\",\"chineseMap\":{\"value\":[{\"code\":\"MMMM\",\"concat\":\" \",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"DD\",\"concat\":\",\",\"displayName\":\"日\",\"id\":\"D\",\"options\":[{\"code\":\"DD\",\"displayName\":\"01\",\"id\":\"CDD\"},{\"code\":\"D\",\"displayName\":\"1\",\"id\":\"CD\"}]},{\"code\":\"YYYY\",\"concat\":\"\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]}]},\"chineseYearMonth\":\"MMMM YYYY\",\"chineseYearMonthMap\":{\"value\":[{\"code\":\"MMMM\",\"concat\":\" \",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"YYYY\",\"concat\":\"\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]}]},\"hyphen\":\"MMMM-DD-YYYY\",\"hyphenMap\":{\"value\":[{\"code\":\"MMMM\",\"concat\":\"-\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"DD\",\"concat\":\"-\",\"displayName\":\"日\",\"id\":\"D\",\"options\":[{\"code\":\"DD\",\"displayName\":\"01\",\"id\":\"CDD\"},{\"code\":\"D\",\"displayName\":\"1\",\"id\":\"CD\"}]},{\"code\":\"YYYY\",\"concat\":\"\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]}]},\"hyphenYearMonth\":\"MMMM-YYYY\",\"hyphenYearMonthMap\":{\"value\":[{\"code\":\"MMMM\",\"concat\":\"-\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"YYYY\",\"concat\":\"\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]}]},\"slash\":\"MM/DD/YYYY\",\"slashMap\":{\"value\":[{\"code\":\"MM\",\"concat\":\"/\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"DD\",\"concat\":\"/\",\"displayName\":\"日\",\"id\":\"D\",\"options\":[{\"code\":\"DD\",\"displayName\":\"01\",\"id\":\"CDD\"},{\"code\":\"D\",\"displayName\":\"1\",\"id\":\"CD\"}]},{\"code\":\"YYYY\",\"concat\":\"\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]}]},\"slashYearMonth\":\"MM/YYYY\",\"slashYearMonthMap\":{\"value\":[{\"code\":\"MM\",\"concat\":\"/\",\"displayName\":\"月\",\"id\":\"M\",\"options\":[{\"code\":\"MMMM\",\"displayName\":\"六月\",\"id\":\"CMMMM\"},{\"code\":\"M月\",\"displayName\":\"6月\",\"id\":\"CM月\"},{\"code\":\"M\",\"displayName\":\"6\",\"id\":\"CM\"},{\"code\":\"MM\",\"displayName\":\"06\",\"id\":\"CMM\"}]},{\"code\":\"YYYY\",\"concat\":\"\",\"displayName\":\"年\",\"id\":\"Y\",\"options\":[{\"code\":\"YYYY\",\"displayName\":\"2024\",\"id\":\"CYYYY\"},{\"code\":\"YY\",\"displayName\":\"24\",\"id\":\"CYY\"}]}]}}", ResourceDateFormat.class))
            .setDecimalPoint(".")
            .setDirection(DirectionEnum.LTR)
            .setGroupingRule("3")
            .setIsoCode("en")
            .setName("English")
            .setThousandsSep(",")
            .setTimeFormat("HH:mm:ss")
            .setResourceTimeFormat(JsonUtils.parseObject("{\"apColonNormal\":\"hh:mm:ssA\",\"apColonNormalMap\":{\"value\":[{\"code\":\"hh\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]},{\"code\":\"A\",\"concat\":\"\",\"displayName\":\"上午/下午\",\"id\":\"A\",\"options\":[{\"code\":\"A\",\"displayName\":\"上午/下午\",\"id\":\"CA\"}],\"width\":\"80px\"}]},\"apColonNormalSss\":\"hh:mm:ss.SSSA\",\"apColonNormalSssMap\":{\"value\":[{\"code\":\"hh\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]},{\"code\":\"SSS\",\"concat\":\".\",\"displayName\":\"毫秒\",\"id\":\"SS\",\"options\":[{\"code\":\"sss\",\"displayName\":\"001\",\"id\":\"Csss\"}]},{\"code\":\"A\",\"concat\":\"\",\"displayName\":\"上午/下午\",\"id\":\"A\",\"options\":[{\"code\":\"A\",\"displayName\":\"上午/下午\",\"id\":\"CA\"}],\"width\":\"80px\"}]},\"apColonShort\":\"hh:mmA\",\"apColonShortMap\":{\"value\":[{\"code\":\"hh\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\"\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"A\",\"concat\":\"\",\"displayName\":\"上午/下午\",\"id\":\"A\",\"options\":[{\"code\":\"A\",\"displayName\":\"上午/下午\",\"id\":\"CA\"}],\"width\":\"80px\"}]},\"colonNormal\":\"HH:mm:ss\",\"colonNormalMap\":{\"value\":[{\"code\":\"HH\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]}]},\"colonNormal\":\"HH:mm:ss\",\"colonNormalMap\":{\"value\":[{\"code\":\"HH\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\":\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]},{\"code\":\"ss\",\"concat\":\"\",\"displayName\":\"秒钟\",\"id\":\"S\",\"options\":[{\"code\":\"ss\",\"displayName\":\"06\",\"id\":\"Css\"},{\"code\":\"s\",\"displayName\":\"6\",\"id\":\"Cs\"}]},{\"code\":\"SSS\",\"concat\":\".\",\"displayName\":\"毫秒\",\"id\":\"SS\",\"options\":[{\"code\":\"sss\",\"displayName\":\"001\",\"id\":\"Csss\"}]}]},\"colonShort\":\"HH:mm\",\"colonShortMap\":{\"value\":[{\"code\":\"HH\",\"concat\":\":\",\"displayName\":\"小时\",\"id\":\"H\",\"options\":[{\"code\":\"h\",\"displayName\":\"1-12\",\"id\":\"Ch\",\"showName\":\"1\"},{\"code\":\"hh\",\"displayName\":\"01-12\",\"id\":\"Chh\",\"showName\":\"01\"},{\"code\":\"H\",\"displayName\":\"1-24\",\"id\":\"CH\",\"showName\":\"13\"},{\"code\":\"HH\",\"displayName\":\"01-24\",\"id\":\"CHH\",\"showName\":\"01\"}]},{\"code\":\"mm\",\"concat\":\"\",\"displayName\":\"分钟\",\"id\":\"M\",\"options\":[{\"code\":\"mm\",\"displayName\":\"06\",\"id\":\"Cmm\"},{\"code\":\"m\",\"displayName\":\"6\",\"id\":\"Cm\"}]}]}}", ResourceTimeFormat.class))
            .setWeekStart(WeekStartEnum.SUNDAY)
            .setTimezoneType(TimeZoneTypeEnum.America_Anchorage)
            .setCalendarType(CalendarTypeEnum.Solar)
            .setInstallState(Boolean.TRUE)
            .setActive(ActiveEnum.ACTIVE)
            .setCode("en-US");

    ResourceCurrency CNY = (ResourceCurrency) new ResourceCurrency()
            .setSymbol("￥")
            .setName("人民币")
            .setRounding(CurrencyRoundingEnum.ROUND_HALF_UP)
            .setPosition(CurrencyPositionEnum.BEFORE)
            .setDecimalPlaces(2)
            .setCurrencyUnitLabel("元")
            .setCurrencySubunitLabel("分")
            .setActive(Boolean.TRUE)
            .setCode(CURRENCY_CODE);

    ResourceCurrency USD = (ResourceCurrency) new ResourceCurrency()
            .setSymbol("$")
            .setName("美元")
            .setRounding(CurrencyRoundingEnum.ROUND_HALF_UP)
            .setPosition(CurrencyPositionEnum.BEFORE)
            .setDecimalPlaces(2)
            .setCurrencyUnitLabel("美元")
            .setCurrencySubunitLabel("美分")
            .setActive(Boolean.TRUE)
            .setCode("USD");

    String DEFAULT_CURRENCY_KEY = "defaultCurrency";

    ResourceConfig DEFAULT_CURRENCY = new ResourceConfig()
            .setKey(DEFAULT_CURRENCY_KEY)
            .setValue(CNY.getCode())
            .setTtype(TtypeEnum.STRING);

    String DEFAULT_COUNTRY_KEY = "defaultCountry";

    ResourceConfig DEFAULT_COUNTRY = new ResourceConfig()
            .setKey(DEFAULT_COUNTRY_KEY)
            .setValue(COUNTRY_CODE)
            .setTtype(TtypeEnum.STRING);

    String DEFAULT_LANG_KEY = "defaultLang";

    ResourceConfig DEFAULT_LANG = new ResourceConfig()
            .setKey(DEFAULT_LANG_KEY)
            .setValue(CHINESE_LANGUAGE_CODE)
            .setTtype(TtypeEnum.STRING);

    String DEFAULT_TIME_ZONE_KEY = "defaultTimeZone";

    String SHANG_HAI_TIME_ZONE_VALUE = "Asia/Shanghai";

    ResourceConfig DEFAULT_TIME_ZONE = new ResourceConfig()
            .setKey(DEFAULT_TIME_ZONE_KEY)
            .setValue(SHANG_HAI_TIME_ZONE_VALUE)
            .setTtype(TtypeEnum.STRING);

    String DEFAULT_BASE_TIME_ZONE_KEY = "defaultBaseTimeZone";

    ResourceConfig DEFAULT_BASE_TIME_ZONE = new ResourceConfig()
            .setKey(DEFAULT_BASE_TIME_ZONE_KEY)
            .setValue(SHANG_HAI_TIME_ZONE_VALUE)
            .setTtype(TtypeEnum.STRING);

    String CHAT_URL_KEY = "chatUrl";

    ResourceConfig CHART_URL = new ResourceConfig()
            .setKey(CHAT_URL_KEY)
            .setValue(FileClientFactory.getClient().getStaticUrl() + "/upload/2020/05/15/1589523634776-%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.png")
            .setTtype(TtypeEnum.TEXT);

    String CHANNEL_URL_KEY = "channelUrl";

    ResourceConfig CHANNEL_URL = new ResourceConfig()
            .setKey(CHANNEL_URL_KEY)
            .setValue(FileClientFactory.getClient().getStaticUrl() + "/upload/2020/05/15/1589523634776-%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.png")
            .setTtype(TtypeEnum.TEXT);

    String MODEL_MAIL_URL_KEY = "modelMailUrl";

    ResourceConfig MODEL_MAIL_URL = new ResourceConfig()
            .setKey(MODEL_MAIL_URL_KEY)
            .setValue(FileClientFactory.getClient().getStaticUrl() + "/upload/2019/09/03/1567523460690-模型消息.png")
            .setTtype(TtypeEnum.TEXT);

    String SYSTEM_MAIL_URL_KEY = "systemMailUrl";

    ResourceConfig SYSTEM_MAIL_URL = new ResourceConfig()
            .setKey(SYSTEM_MAIL_URL_KEY)
            .setValue(FileClientFactory.getClient().getStaticUrl() + "/upload/2019/09/03/1567523507231-系统消息.png")
            .setTtype(TtypeEnum.TEXT);

    String DEFAULT_BRAND_LOGO = FileClientFactory.getClient().getStaticUrl() + "/pamirs/image/default_brand_logo.png";

    /**
     * @deprecated please using CommonConstants#getDefaultAppLogoUrl
     */
    @Deprecated
    String DEFAULT_APP_LOGO = FileClientFactory.getClient().getStaticUrl() + "/pamirs/image/logo/default.png";

}
