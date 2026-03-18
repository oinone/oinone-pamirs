package pro.shushi.pamirs.resource.api.init;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.ResourceModule;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.resource.api.tmodel.ResourceRegionProxyModel;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceDateFormat;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceTimeFormat;
import pro.shushi.pamirs.resource.api.util.UnGroupData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: Wuer
 * @email: syj@shushi.pro
 * @Date: 2020/4/29 2:09 下午
 * @Description:
 */
@Order(90)
@Component
public class ResourceInit implements MetaDataEditor, InstallDataInit, UpgradeDataInit {

    @Override
    public List<String> modules() {
        return Collections.singletonList(ResourceModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, ResourceModule.MODULE_MODULE, ResourceModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        viewActionInit(util);
        modifyViewAction(util);
        modifyServerAction(util);
        menuInit(util);
        homepageInit(util);
    }

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        onlyCreateInit();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        onlyCreateInit();
        return true;
    }

    private void onlyCreateInit() {
        initResourceLang();
        initResourceCurrency();
        initResourceCountryGroup();
        initResourceCountry();
        initResourceConfig();
        initResourceTheme();
        initResourceIcon();
    }

    private void initResourceIcon() {
        new ResourceIconGroup().setName(UnGroupData.getName())
                .setSys(Boolean.FALSE)
                .setBatchCode(0L)
                .setId(UnGroupData.ID)
                .createOrUpdate();
    }

    private void viewActionInit(InitializationUtil util) {
        util.createViewAction("ResourceCountryActionKeyword", "国家关键字设置", ResourceCountry.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCountry.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "ResourceCountryFormKeyword");
        util.createViewAction("ResourceProvinceActionKeyword", "省关键字设置", ResourceProvince.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceProvince.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "ResourceProvinceFormKeyword");
        util.createViewAction("ResourceCityActionKeyword", "市关键字设置", ResourceCity.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCity.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "ResourceCityFormKeyword");
        util.createViewAction("ResourceDistrictActionKeyword", "区关键字设置", ResourceDistrict.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceDistrict.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "ResourceDistrictFormKeyword");
        util.createViewAction("ResourceStreetActionKeyword", "街道关键字设置", ResourceStreet.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceStreet.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "ResourceStreetFormKeyword");

        util.createViewAction("groupCreateCountry", "添加", ResourceCountry.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCountry.MODEL_MODEL, ViewTypeEnum.TABLE, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("countryCreateProvince", "创建", ResourceProvince.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceProvince.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("provinceCreateCity", "创建", ResourceCity.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCity.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("cityCreateDirect", "创建", ResourceDistrict.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceDistrict.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("districtCreateStreet", "创建", ResourceStreet.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceStreet.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);

        util.createViewAction("countryCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("provinceCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("cityCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("districtCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("streetCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("currencyCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("taxCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);

        util.createViewAction("countryCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("provinceCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("cityCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("districtCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("streetCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);

    }

    private void modifyViewAction(InitializationUtil util) {

    }

    private void modifyServerAction(InitializationUtil util) {
        util.modifyServerAction(ResourceCountryGroup.MODEL_MODEL, InitializationUtil.DEFAULT_DELETE, delete -> delete.setDisable("!(context.activeRecords && LIST_COUNT(context.activeRecords) >= 1 && LIST_AND(LIST_FIELD_NOT_IN(context.activeRecords, '" + ResourceCountryGroup.MODEL_MODEL + "','code',['Asia','Europe','Americas','Africa','Oceania'])))"));
        util.modifyServerAction(ResourceCountry.MODEL_MODEL, InitializationUtil.DEFAULT_DELETE, delete -> delete.setDisable("!(context.activeRecords && LIST_COUNT(context.activeRecords) >= 1 && LIST_AND(LIST_FIELD_NOT_EQUALS(context.activeRecords, '" + ResourceCountry.MODEL_MODEL + "','code','CN')))"));
    }

    private void menuInit(InitializationUtil util) {
        //region 地址库
        util.createViewActionMenu("AddressLib", "地址库", 1L, null);
        util.createViewActionMenu("ResourceRegion", "地区", 1L, "AddressLib", ResourceRegionProxyModel.MODEL_MODEL, "ResourceRegionProxyModelTable", viewAction -> viewAction.setFilter("hasChildren == 0"));
        util.createViewActionMenu("ResourceCountryGroup", "国家分组", 2L, "AddressLib", ResourceCountryGroup.MODEL_MODEL, "ResourceCountryGroupTable", null);
        util.createViewActionMenu("ResourceCountry", "国家", 3L, "AddressLib", ResourceCountry.MODEL_MODEL, "ResourceCountryTable", null);
        util.createViewActionMenu("ResourceProvince", "省", 4L, "AddressLib", ResourceProvince.MODEL_MODEL, "ResourceProvinceTable", null);
        util.createViewActionMenu("ResourceCity", "市", 5L, "AddressLib", ResourceCity.MODEL_MODEL, "ResourceCityTable", null);
        util.createViewActionMenu("ResourceDistrict", "区", 6L, "AddressLib", ResourceDistrict.MODEL_MODEL, "ResourceDistrictTable", null);
        util.createViewActionMenu("ResourceStreet", "街道", 7L, "AddressLib", ResourceStreet.MODEL_MODEL, "ResourceStreetTable", null);
        //endregion

        util.createViewActionMenu("ResourceCurrency", "货币", 2L, null, ResourceCurrency.MODEL_MODEL, "ResourceCurrencyTable", null);
        util.createViewActionMenu("ResourceLang", "语言", 3L, null, ResourceLang.MODEL_MODEL, "ResourceLangTable", null);

        util.createViewActionMenu("ResourceTaxConfig", "税率", 4L, null);
        util.createViewActionMenu("ResourceTaxKind", "税种", 1L, "ResourceTaxConfig", ResourceTaxKind.MODEL_MODEL, "ResourceTaxKindTable", null);
        util.createViewActionMenu("ResourceTax", "税率", 2L, "ResourceTaxConfig", ResourceTax.MODEL_MODEL, "ResourceTaxTable", null);

        util.createViewActionMenu("ResourceIcon", "图标管理", 6L, null, ResourceIcon.MODEL_MODEL, "iconManagement", null);
    }

    private void homepageInit(InitializationUtil util) {
        util.setHomepageByMenu("ResourceRegion");
    }

    private void initResourceCountryGroup() {
        DefaultResourceConstants.ASIA.setName(I18nUtils.getMessage("resource.country.group.Asia"));
        DefaultResourceConstants.EUROPE.setName(I18nUtils.getMessage("resource.country.group.Europe"));
        DefaultResourceConstants.AMERICAS.setName(I18nUtils.getMessage("resource.country.group.Americas"));
        DefaultResourceConstants.AFRICA.setName(I18nUtils.getMessage("resource.country.group.Africa"));
        DefaultResourceConstants.OCEANIA.setName(I18nUtils.getMessage("resource.country.group.Oceania"));
        FetchUtil.onlyCreateBatch(CollectionHelper.<ResourceCountryGroup>newInstance()
                .add(DefaultResourceConstants.ASIA)
                .add(DefaultResourceConstants.EUROPE)
                .add(DefaultResourceConstants.AMERICAS)
                .add(DefaultResourceConstants.AFRICA)
                .add(DefaultResourceConstants.OCEANIA)
                .build());
    }

    private void initResourceCountry() {
        DefaultResourceConstants.COUNTRY.setName(I18nUtils.getMessage("resource.country.China"));
        DefaultResourceConstants.COUNTRY.setCompleteName(I18nUtils.getMessage("resource.country.ChinaFull"));
        DefaultResourceConstants.COUNTRY_REGION.setName(DefaultResourceConstants.COUNTRY.getName());
        FetchUtil.onlyCreate(DefaultResourceConstants.COUNTRY);
        FetchUtil.onlyCreate(DefaultResourceConstants.COUNTRY_REGION);
    }

    private void initResourceLang() {
        List<ResourceLang> initLangs = new ArrayList<>();
        List<ResourceLang> updateLangs = new ArrayList<>();
        checkInit(initLangs, updateLangs, DefaultResourceConstants.CHINESE_LANGUAGE);
        checkInit(initLangs, updateLangs, DefaultResourceConstants.ENGLISH_LANGUAGE);
        if (CollectionUtils.isNotEmpty(initLangs)) {
            FetchUtil.onlyCreateBatch(initLangs);
        }
        if (CollectionUtils.isNotEmpty(updateLangs)) {
            Models.origin().updateBatch(updateLangs);
        }
    }

    private static void checkInit(List<ResourceLang> initLangs, List<ResourceLang> updateLangs, ResourceLang resourceLang) {
        ResourceLang lang = resourceLang.queryByCode();
        if (lang != null) {
            ResourceDateFormat resourceDateFormat = lang.getResourceDateFormat();
            ResourceTimeFormat resourceTimeFormat = lang.getResourceTimeFormat();
            if (resourceDateFormat == null || resourceTimeFormat == null) {
                ResourceLang updateLang = new ResourceLang();
                updateLang.setCode(resourceLang.getCode());
                updateLang.setResourceDateFormat(resourceLang.getResourceDateFormat());
                updateLang.setResourceTimeFormat(resourceLang.getResourceTimeFormat());
                updateLangs.add(updateLang);
            } else if (resourceTimeFormat.getApColonNormalSssMap() == null
                    || resourceTimeFormat.getColonNormalSssMap() == null) {
                ResourceLang updateLang = new ResourceLang();
                updateLang.setCode(resourceLang.getCode());
                ResourceTimeFormat originalResourceTimeFormat = lang.getResourceTimeFormat();
                originalResourceTimeFormat.setApColonNormalSss(resourceLang.getResourceTimeFormat().getApColonNormalSss());
                originalResourceTimeFormat.setColonNormalSss(resourceLang.getResourceTimeFormat().getColonNormalSss());
                originalResourceTimeFormat.setApColonNormalSssMap(resourceLang.getResourceTimeFormat().getApColonNormalSssMap());
                originalResourceTimeFormat.setColonNormalSssMap(resourceLang.getResourceTimeFormat().getColonNormalSssMap());
                updateLang.setResourceTimeFormat(originalResourceTimeFormat);
                updateLangs.add(updateLang);
            }

        } else {
            initLangs.add(resourceLang);
        }
    }

    private void initResourceCurrency() {
        DefaultResourceConstants.CNY.setName(I18nUtils.getMessage("resource.currency.CNY"));
        DefaultResourceConstants.CNY.setCurrencyUnitLabel(I18nUtils.getMessage("resource.currency.CNY.unit"));
        DefaultResourceConstants.CNY.setCurrencySubunitLabel(I18nUtils.getMessage("resource.currency.CNY.subunit"));

        DefaultResourceConstants.USD.setName(I18nUtils.getMessage("resource.currency.USD"));
        DefaultResourceConstants.USD.setCurrencyUnitLabel(I18nUtils.getMessage("resource.currency.USD.unit"));
        DefaultResourceConstants.USD.setCurrencySubunitLabel(I18nUtils.getMessage("resource.currency.USD.subunit"));

        FetchUtil.onlyCreateBatch(CollectionHelper.<ResourceCurrency>newInstance()
                .add(DefaultResourceConstants.CNY)
                .add(DefaultResourceConstants.USD)
                .build());
    }

    private void initResourceConfig() {
        FetchUtil.onlyCreateBatch(CollectionHelper.<ResourceConfig>newInstance()
                .add(DefaultResourceConstants.DEFAULT_CURRENCY)
                .add(DefaultResourceConstants.DEFAULT_COUNTRY)
                .add(DefaultResourceConstants.DEFAULT_LANG)
                .add(DefaultResourceConstants.DEFAULT_TIME_ZONE)
                .add(DefaultResourceConstants.DEFAULT_BASE_TIME_ZONE)
                .add(new ResourceConfig()
                        .setKey(DefaultResourceConstants.CHAT_URL_KEY)
                        .setValue(FileClientFactory.getClient().getStaticUrl() + "/images/resource/default-avatar.png")
                        .setTtype(TtypeEnum.TEXT))
                .add(new ResourceConfig()
                        .setKey(DefaultResourceConstants.CHANNEL_URL_KEY)
                        .setValue(FileClientFactory.getClient().getStaticUrl() + "/images/resource/default-avatar.png")
                        .setTtype(TtypeEnum.TEXT))
                .add(new ResourceConfig()
                        .setKey(DefaultResourceConstants.MODEL_MAIL_URL_KEY)
                        .setValue(FileClientFactory.getClient().getStaticUrl() + "/images/resource/model-message-avatar.png")
                        .setTtype(TtypeEnum.TEXT))
                .add(new ResourceConfig()
                        .setKey(DefaultResourceConstants.SYSTEM_MAIL_URL_KEY)
                        .setValue(FileClientFactory.getClient().getStaticUrl() + "/images/resource/system-message-avatar.png")
                        .setTtype(TtypeEnum.TEXT))
                .build());
    }

    private void initResourceTheme() {
    }
}
