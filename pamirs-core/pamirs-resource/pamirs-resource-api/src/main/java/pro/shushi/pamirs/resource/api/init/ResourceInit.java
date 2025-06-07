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
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.ResourceModule;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.resource.api.pojo.UnGroup;
import pro.shushi.pamirs.resource.api.tmodel.ResourceRegionProxyModel;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceDateFormat;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceTimeFormat;

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
        UnGroup.getUnGroup();
    }

    private void viewActionInit(InitializationUtil util) {
        util.createViewAction("国家关键字设置dialog", "国家关键字设置", ResourceCountry.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCountry.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "国家关键字设置form");
        util.createViewAction("省关键字设置dialog", "省关键字设置", ResourceProvince.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceProvince.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "省关键字设置form");
        util.createViewAction("市关键字设置dialog", "市关键字设置", ResourceCity.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCity.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "市关键字设置form");
        util.createViewAction("区关键字设置dialog", "区关键字设置", ResourceDistrict.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceDistrict.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "区关键字设置form");
        util.createViewAction("街道关键字设置dialog", "街道关键字设置", ResourceStreet.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceStreet.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "街道关键字设置form");

        util.createViewAction("groupCreateCountry", "添加", ResourceCountry.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCountry.MODEL_MODEL, ViewTypeEnum.TABLE, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "国家分组form");
        util.createViewAction("countryCreateProvince", "创建", ResourceProvince.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceProvince.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "国家form");
        util.createViewAction("provinceCreateCity", "创建", ResourceCity.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceCity.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "省form");
        util.createViewAction("cityCreateDirect", "创建", ResourceDistrict.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceDistrict.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "市form");
        util.createViewAction("districtCreateStreet", "创建", ResourceStreet.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceStreet.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "区form");

        util.createViewAction("countryCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "国家form");
        util.createViewAction("provinceCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "省form");
        util.createViewAction("cityCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "市form");
        util.createViewAction("districtCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "区form");
        util.createViewAction("streetCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "街道form");
        util.createViewAction("currencyCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "货币form");
        util.createViewAction("taxCreateOutResourceRelation", "创建", OutResourceRelation.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), OutResourceRelation.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "税率form");

        util.createViewAction("countryCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "国家关键字设置form");
        util.createViewAction("provinceCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "省关键字设置form");
        util.createViewAction("cityCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "市关键字设置form");
        util.createViewAction("districtCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "区关键字设置form");
        util.createViewAction("streetCreateResourceRegionMapping", "创建", ResourceRegionMapping.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), ResourceRegionMapping.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "街道关键字设置form");

    }

    private void modifyViewAction(InitializationUtil util) {

    }

    private void modifyServerAction(InitializationUtil util) {
        util.modifyServerAction(ResourceCountryGroup.MODEL_MODEL, InitializationUtil.DEFAULT_DELETE, delete -> delete.setDisable("!(context.activeRecords && LIST_COUNT(context.activeRecords) >= 1 && LIST_AND(LIST_FIELD_NOT_IN(context.activeRecords, '" + ResourceCountryGroup.MODEL_MODEL + "','code',['Asia','Europe','Americas','Africa','Oceania'])))"));
        util.modifyServerAction(ResourceCountry.MODEL_MODEL, InitializationUtil.DEFAULT_DELETE, delete -> delete.setDisable("!(context.activeRecords && LIST_COUNT(context.activeRecords) >= 1 && LIST_AND(LIST_FIELD_NOT_EQUALS(context.activeRecords, '" + ResourceCountry.MODEL_MODEL + "','code','CN')))"));
    }

    private void menuInit(InitializationUtil util) {
        //region 地址库
        util.createViewActionMenu("地址库", "地址库", 1L, null);
        util.createViewActionMenu("地区", "地区", 1L, "地址库", ResourceRegionProxyModel.MODEL_MODEL, "地区列表", "地区table", viewAction -> viewAction.setFilter("hasChildren == 0"));
        util.createViewActionMenu("国家分组", "国家分组", 2L, "地址库", ResourceCountryGroup.MODEL_MODEL, "国家分组table", null);
        util.createViewActionMenu("国家", "国家", 3L, "地址库", ResourceCountry.MODEL_MODEL, "国家table", null);
        util.createViewActionMenu("省", "省", 4L, "地址库", ResourceProvince.MODEL_MODEL, "省table", null);
        util.createViewActionMenu("市", "市", 5L, "地址库", ResourceCity.MODEL_MODEL, "市table", null);
        util.createViewActionMenu("区", "区", 6L, "地址库", ResourceDistrict.MODEL_MODEL, "区table", null);
        util.createViewActionMenu("街道", "街道", 7L, "地址库", ResourceStreet.MODEL_MODEL, "街道table", null);
        //endregion

        util.createViewActionMenu("货币", "货币", 2L, null, ResourceCurrency.MODEL_MODEL, "货币table", null);
        util.createViewActionMenu("语言", "语言", 3L, null, ResourceLang.MODEL_MODEL, "语言table", null);

        util.createViewActionMenu("税率配置", "税率", 4L, null);
        util.createViewActionMenu("税种", "税种", 1L, "税率配置", ResourceTaxKind.MODEL_MODEL, "税种table", null);
        util.createViewActionMenu("税率", "税率", 2L, "税率配置", ResourceTax.MODEL_MODEL, "税率table", null);

        util.createViewActionMenu("图标管理", "图标管理", 6L, null, ResourceIcon.MODEL_MODEL, "iconManagement", null);
    }

    private void homepageInit(InitializationUtil util) {
        util.setHomepageByMenu("地区");
    }

    private void initResourceCountryGroup() {
        FetchUtil.onlyCreateBatch(CollectionHelper.<ResourceCountryGroup>newInstance()
                .add(DefaultResourceConstants.ASIA)
                .add(DefaultResourceConstants.EUROPE)
                .add(DefaultResourceConstants.AMERICAS)
                .add(DefaultResourceConstants.AFRICA)
                .add(DefaultResourceConstants.OCEANIA)
                .build());
    }

    private void initResourceCountry() {
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
            }
        } else {
            initLangs.add(resourceLang);
        }
    }

    private void initResourceCurrency() {
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
                .add(DefaultResourceConstants.CHART_URL)
                .add(DefaultResourceConstants.CHANNEL_URL)
                .add(DefaultResourceConstants.MODEL_MAIL_URL)
                .add(DefaultResourceConstants.SYSTEM_MAIL_URL)
                .build());
    }

    private void initResourceTheme() {
    }
}
