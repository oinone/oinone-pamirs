package pro.shushi.pamirs.apps.view.action;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.tmodel.AppConfigTransient;
import pro.shushi.pamirs.apps.api.tmodel.AppMenu;
import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.web.loader.PageLoadAction;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.sys.setting.api.CompanySettingsService;
import pro.shushi.pamirs.sys.setting.tmodel.CompanySettings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static pro.shushi.pamirs.apps.api.enmu.AppsExpEnumerate.*;

/**
 * @author shier
 * date  2022/9/1 12:10 下午
 */
@Slf4j
@Base
@Component
@Model.model(AppConfigTransient.MODEL_MODEL)
public class AppSettingAction {

    @Autowired
    AppConfigService appConfigService;
    @Autowired
    private CompanySettingsService companySettingsService;

    @Autowired
    private PageLoadAction pageLoadAction;

    private final Pattern EN_LETTER_NUM = Pattern.compile("^[a-z][a-z0-9]*$");
    private final Pattern NUM_START     = Pattern.compile("^\\d+?.*$");

    @Action(displayName = "企业设置详情")
    public AppConfigTransient gatewaySetting(String companyCode) {
        AppConfigTransient appConfigTransient = new AppConfigTransient();

        CompanySettings settings = companySettingsService.queryOne(companyCode);
        if (null != settings) {
            Long times = companySettingsService.domainChangeTimes(companyCode);
            appConfigTransient.setLogo(settings.getLogo());
            appConfigTransient.setDomain(settings.getDomainName());
            appConfigTransient.setSearchable(settings.getSearchable());
            String tenant = settings.getTenant();
            tenant = StringUtils.isBlank(tenant) ? PamirsTenantSession.getTenant() : tenant;
            appConfigTransient.setTenant(tenant);
            appConfigTransient.setCompanyCode(settings.getCompanyCode());
            appConfigTransient.setDomainTimes(3L - times);
        }

        AppConfig appConfig = new AppConfig().setScope(AppConfigScopeEnum.GLOBAL)
                .setCode(AppConfig.generateCode(AppConfigScopeEnum.GLOBAL))
                .setCompanyCode(companyCode)
                .queryOne();
        if(appConfig == null){
            appConfigTransient.setWorkbenchAsFist(Boolean.TRUE);
        }else {
            AppMenu menus = queryMenuTree(appConfig.getApp(), appConfig.getHomePageModel(), appConfig.getHomePageName());
            UeModule module = new UeModule().setModule(appConfig.getApp()).queryOne();

            appConfigTransient.setGatewayMenu(menus);
            appConfigTransient.setGatewayModule(module);
            appConfigTransient.setWorkbenchAsFist(appConfig.getDefaultHomePage());
        }

        return appConfigTransient;
    }

    @Action(displayName = "产品设置详情")
    public AppConfigTransient gatewayModuleSetting(String module) {
        AppConfigTransient appConfigTransient = new AppConfigTransient();
        AppConfig appConfig = appConfigService.fetchAppConfig(module);
        log.info("产品设置详情:[{}]", JsonUtils.toJSONString(appConfig));
        AppMenu menus;
        if (null != appConfig) {
            appConfigTransient.setLogo(appConfig.getLogo());
            String homePageModel = appConfig.getHomePageModel();
            String homePageName = appConfig.getHomePageName();
            menus = queryMenuTree(module, homePageModel, homePageName);
            UeModule ueModule = new UeModule().setModule(module).queryOne();
            appConfigTransient.setGatewayModule(ueModule);
        } else {
            UeModule ueModule = new UeModule().setModule(module).queryOne();
            menus = queryMenuTree(module, ueModule.getHomePageModel(), ueModule.getHomePageName());
        }
        appConfigTransient.setGatewayMenu(menus);
        return appConfigTransient;
    }

    @Action(displayName = "企业设置保存")
    public AppConfigTransient saveGatewaySetting(AppConfigTransient data) {

        String companyCode = data.getCompanyCode();
        if (StringUtils.isBlank(companyCode)) {
            throw PamirsException.construct(APPS_ENT_SETTING_ILLEGAL)
                    .errThrow();
        }

        String domain = data.getDomain();
        if (StringUtils.isNotBlank(domain)) {
            int len = StringUtils.length(domain);
            if (len > 12 || len < 5) {
                throw PamirsException.construct(APPS_ENT_SETTING_DOMAIN_LENGTH)
                        .errThrow();
            }

            if (!EN_LETTER_NUM.matcher(domain).matches()) {
                throw PamirsException.construct(APPS_ENT_SETTING_DOMAIN_EN_NUM)
                        .errThrow();
            }

            if (NUM_START.matcher(domain).matches()) {
                throw PamirsException.construct(APPS_ENT_SETTING_DOMAIN_EN_START)
                        .errThrow();
            }

            Long count = companySettingsService.checkDomainUnique(companyCode, data.getDomain());
            if (count > 0) {
                throw PamirsException.construct(APPS_ENT_SETTING_DOMAIN_EXISTED)
                        .errThrow();
            }
        }

        AppConfig appConfig = new AppConfig();
        appConfig.setCode(AppConfig.generateCode(AppConfigScopeEnum.GLOBAL));
        appConfig.setScope(AppConfigScopeEnum.GLOBAL);
        appConfig.setCompanyCode(companyCode);
        appConfig.setDefaultHomePage(data.getWorkbenchAsFist());
        if (null != data.getWorkbenchAsFist()) {
            if (!data.getWorkbenchAsFist()) {
                //不设置工作台为首页
                UeModule ueModule = null;
                if (null != data.getGatewayModule()
                        && StringUtils.isNotBlank(data.getGatewayModule().getModule())
                        && !StringUtils.equals(data.getGatewayModule().getModule(), "undefined")) {

                    String module = data.getGatewayModule().getModule();
                    IWrapper<UeModule> qw = Pops.<UeModule>lambdaQuery()
                            .from(UeModule.MODEL_MODEL)
                            .eq(UeModule::getModule, module);

                    ueModule = new UeModule().queryOneByWrapper(qw);
                    appConfig.setApp(data.getGatewayModule().getModule());
                } else {
                    ueModule = firstMenuAndModule();
                }

                if (null != ueModule) {
                    appConfig.setHomePageName(ueModule.getHomePageName()).setHomePageModel(ueModule.getHomePageModel()).setApp(ueModule.getModule());
                }
            } else {
                //设置工作台为首页
                appConfig.setHomePageName(null).setHomePageModel(null).setApp(null);
            }
        }
        if (null != data.getGatewayMenu()) {
            appConfig.setApp(data.getGatewayModule().getModule());
            AppMenu mainMenu = data.getGatewayMenu();
            if (mainMenu != null && mainMenu.getId() != null) {
                mainMenu = mainMenu.queryById();
                if (!data.getWorkbenchAsFist() && data.getGatewayMenu() != null) {
                    appConfig.setHomePageName(mainMenu.getActionName()).setHomePageModel(mainMenu.getModel());
                }
            }
        }

        if (null != data.getLogo()) {
            appConfig.setLogo(data.getLogo());
        }

        CompanySettings comSet = new CompanySettings();
        comSet.setCompanyCode(companyCode);
        comSet.setDomainName(domain);
        if (null != data.getLogo()) {
            appConfig.setLogo(data.getLogo());
            comSet.setLogo(data.getLogo());
        }
        if (null != data.getSearchable()) {
            comSet.setSearchable(data.getSearchable());
        }

        comSet = companySettingsService.save(comSet);
        appConfigService.save(appConfig);
        AppConfigTransient result = gatewaySetting(companyCode);
        String tenant = comSet.getTenant();
        tenant = StringUtils.isBlank(tenant) ? PamirsTenantSession.getTenant() : tenant;
        result.setTenant(tenant);
        result.setDomain(comSet.getDomainName());
        result.setSearchable(comSet.getSearchable());
        result.setLogo(comSet.getLogo());
        log.info("企业设置保存:[{}]", JsonUtils.toJSONString(result));
        return result;
    }

    @Action(displayName = "产品设置保存")
    public AppConfigTransient saveModuleSetting(AppConfigTransient data) {
        String code = AppConfig.generateCode(AppConfigScopeEnum.APP, data.getGatewayModule().getModule());
        AppConfig appConfig = new AppConfig();
        appConfig.setScope(AppConfigScopeEnum.APP);
        appConfig.setCompanyCode(data.getCompanyCode());
        appConfig.setCode(code);
        appConfig.setApp(data.getGatewayModule().getModule());
        if (data.getGatewayMenu() != null) {
            AppMenu gatewayMenu = data.getGatewayMenu();
            if (gatewayMenu.getId() != null) {
                gatewayMenu = gatewayMenu.queryById();
            }
            appConfig.setHomePageModel(gatewayMenu.getModel())
                    .setHomePageName(gatewayMenu.getActionName());
        }
        if (data.getLogo() != null) {
            appConfig.setLogo(data.getLogo());
        }
        appConfigService.save(appConfig);
        String homePageModel = appConfig.getHomePageModel();
        String homePageName = appConfig.getHomePageName();
        AppMenu menus = queryMenuTree(appConfig.getApp(), homePageModel, homePageName);
        data.setGatewayMenu(menus);
        return data;
    }

    private AppMenu queryMenuTree(String module, String model, String viewActionName) {
        if (StringUtils.isBlank(model) || StringUtils.isBlank(viewActionName)) {
            return null;
        }
        AppMenu moduleMenus = new AppMenu().setModel(model).setModule(module).setActionName(viewActionName).queryOne();
        return moduleMenus;
    }

    private List<AppMenu> queryModuleTree(String module, String model, String viewActionName) {
        if (StringUtils.isBlank(model) || StringUtils.isBlank(viewActionName)) {
            return new ArrayList<>();
        }
        List<AppMenu> moduleMenus = new AppMenu().setModule(module).queryList();
        if (null == moduleMenus) {
            return new ArrayList<>();
        }
        AppMenu mainMenu = moduleMenus.stream().filter(v -> model.equals(v.getModel()) && viewActionName.equals(v.getActionName())).findFirst().orElse(null);
        if (mainMenu != null) {
            List<AppMenu> result = new ArrayList<>();
            fetchParentMenu(mainMenu, moduleMenus, result);
            return result;
        }
        return new ArrayList<>();
    }

    private void fetchParentMenu(AppMenu current, List<AppMenu> menus, List<AppMenu> result) {
        result.add(current);
        if (StringUtils.isNotBlank(current.getParentName())) {
            AppMenu parentMenu = menus.stream().filter(v -> current.getParentName().equals(v.getName())).findFirst().orElse(null);
            if (parentMenu != null) {
                fetchParentMenu(parentMenu, menus, result);
            }
        }
    }

    public UeModule firstMenuAndModule() {
        IWrapper<UeModule> wrapper = Pops.<UeModule>lambdaQuery()
                .from(UeModule.MODEL_MODEL)
                .in(UeModule::getState, Lists.newArrayList(ModuleStateEnum.INSTALLED.value(), ModuleStateEnum.TOUPGRADE.value()));
        return Optional.ofNullable(new UeModule().queryList(wrapper))
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(v -> null != v.getHomePage())
                .filter(ModuleDefinition::getApplication)
                .min(Comparator.comparing(ModuleDefinition::getPriority))
                .orElse(null);
//        String homePageModel = ueModule.getHomePageModel();
//        String homePageName = ueModule.getHomePageName();
//        redirectMenu = queryMenuTree(ueModule.getModule(), homePageModel, homePageName);
    }

}
