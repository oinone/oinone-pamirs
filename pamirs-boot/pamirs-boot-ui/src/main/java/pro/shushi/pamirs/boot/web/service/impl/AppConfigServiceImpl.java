package pro.shushi.pamirs.boot.web.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate.BASE_APP_CONFIG_PARAM_ERROR;

/**
 * 基础配置服务实现
 * <p>
 * 配置优先级：产品应用配置 > 产品通用配置 > 应用配置 > 全局配置
 * <p>
 * 2022/2/22 10:44 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * @see AppConfigScopeEnum
 */
@Service
public class AppConfigServiceImpl implements AppConfigService {

    @Override
    public AppConfig save(AppConfig appConfig) {
        appConfig.createOrUpdate();
        return appConfig;
    }

    @Override
    public AppConfig fetchRequestAppConfig(String product, String app) {
        AppConfig result = new AppConfig();
        Map<AppConfigScopeEnum, AppConfig> appConfigMap = fetchAppConfigMap(product, app);
        if (null == appConfigMap) {
            return result.setApp(app);
        }

        // 获取入口应用
        String home = app;
        if (StringUtils.isBlank(home)) {
            if (StringUtils.isNotBlank(product)) {
                home = AppConfigHandler.fetchValidValue(appConfigMap.get(AppConfigScopeEnum.PRODUCT), AppConfig::getApp);
            }
            if (StringUtils.isBlank(home)) {
                home = AppConfigHandler.fetchValidValue(appConfigMap.get(AppConfigScopeEnum.GLOBAL), AppConfig::getApp);
            }
        }
        result.setApp(home);

        // 获取应用配置
        AppConfigHandler.getInstance(appConfigMap, result)
                // 获取主题配置
                .config(AppConfig::getTheme, AppConfig::setTheme, false)
                // 获取母版配置
                .config(AppConfig::getMask, AppConfig::setMask, false)
                // 获取logo
                .config(AppConfig::getLogo, AppConfig::setLogo, false)
                // 获取登录页logo
                .config(AppConfig::getLoginPageLogo, AppConfig::setLoginPageLogo, false)
                // 获取favicon
                .config(AppConfig::getFavicon, AppConfig::setFavicon, false)
                // 获取是否使用默认首页
                .configBoolean(AppConfig::getDefaultHomePage, AppConfig::setDefaultHomePage, false)
                // 获取首页
                .config(AppConfig::getHomePageModel, AppConfig::setHomePageModel,
                        AppConfig::getHomePageName, AppConfig::setHomePageName, !StringUtils.isBlank(app));

        return result;
    }

    static class AppConfigHandler {

        private final Map<AppConfigScopeEnum, AppConfig> appConfigMap;

        private final AppConfig appConfig;

        public AppConfigHandler(Map<AppConfigScopeEnum, AppConfig> appConfigMap, AppConfig appConfig) {
            this.appConfigMap = appConfigMap;
            this.appConfig = appConfig;
        }

        public static AppConfigHandler getInstance(Map<AppConfigScopeEnum, AppConfig> appConfigMap, AppConfig appConfig) {
            return new AppConfigHandler(appConfigMap, appConfig);
        }

        private AppConfigHandler config(Getter<AppConfig, String> getter,
                                        BiFunction<AppConfig, String, AppConfig> setter,
                                        boolean excludeGlobalConfig) {
            setter.apply(appConfig, fetchHighPriorityValue(appConfigMap, getter, excludeGlobalConfig));
            return this;
        }

        private AppConfigHandler configBoolean(Getter<AppConfig, Boolean> getter,
                                               BiFunction<AppConfig, Boolean, AppConfig> setter,
                                               boolean excludeGlobalConfig) {
            setter.apply(appConfig, fetchHighPriorityBooleanValue(appConfigMap, getter, excludeGlobalConfig));
            return this;
        }

        public AppConfigHandler config(Getter<AppConfig, String> getter1,
                                       BiFunction<AppConfig, String, AppConfig> setter1,
                                       Getter<AppConfig, String> getter2,
                                       BiFunction<AppConfig, String, AppConfig> setter2,
                                       boolean excludeGlobalConfig) {
            if (null == appConfigMap) {
                return this;
            }
            if (fetchRelatedHighPriorityValue0(appConfigMap.get(AppConfigScopeEnum.PRODUCT_APP),
                    appConfig, getter1, setter1, getter2, setter2)) {
                return this;
            }
            if (fetchRelatedHighPriorityValue0(appConfigMap.get(AppConfigScopeEnum.PRODUCT),
                    appConfig, getter1, setter1, getter2, setter2)) {
                return this;
            }
            if (fetchRelatedHighPriorityValue0(appConfigMap.get(AppConfigScopeEnum.APP),
                    appConfig, getter1, setter1, getter2, setter2)) {
                return this;
            }
            if (!excludeGlobalConfig) {
                fetchRelatedHighPriorityValue0(appConfigMap.get(AppConfigScopeEnum.GLOBAL),
                        appConfig, getter1, setter1, getter2, setter2);
            }
            return this;
        }

        private static boolean fetchRelatedHighPriorityValue0(AppConfig config,
                                                              AppConfig result,
                                                              Getter<AppConfig, String> getter1,
                                                              BiFunction<AppConfig, String, AppConfig> setter1,
                                                              Getter<AppConfig, String> getter2,
                                                              BiFunction<AppConfig, String, AppConfig> setter2) {
            String value1 = fetchValidValue(config, getter1);
            String value2 = fetchValidValue(config, getter2);
            if (StringUtils.isNotBlank(value1)) {
                setter1.apply(result, value1);
                setter2.apply(result, value2);
                return true;
            }
            return false;
        }

        public static String fetchHighPriorityValue(Map<AppConfigScopeEnum, AppConfig> appConfigMap,
                                                    Getter<AppConfig, String> getter,
                                                    boolean excludeGlobalConfig) {
            if (null == appConfigMap) {
                return null;
            }
            String value = fetchValidValue(appConfigMap.get(AppConfigScopeEnum.PRODUCT_APP), getter);
            if (StringUtils.isBlank(value)) {
                value = fetchValidValue(appConfigMap.get(AppConfigScopeEnum.PRODUCT), getter);
            }
            if (StringUtils.isBlank(value)) {
                value = fetchValidValue(appConfigMap.get(AppConfigScopeEnum.APP), getter);
            }
            if (StringUtils.isBlank(value) && !excludeGlobalConfig) {
                value = fetchValidValue(appConfigMap.get(AppConfigScopeEnum.GLOBAL), getter);
            }
            return value;
        }

        public static String fetchValidValue(AppConfig appConfig, Getter<AppConfig, String> getter) {
            return Optional.ofNullable(appConfig)
                    .map(getter).filter(StringUtils::isNotBlank).orElse(null);
        }

        public static Boolean fetchHighPriorityBooleanValue(Map<AppConfigScopeEnum, AppConfig> appConfigMap,
                                                            Getter<AppConfig, Boolean> getter,
                                                            boolean excludeGlobalConfig) {
            if (null == appConfigMap) {
                return null;
            }
            Boolean value = fetchValidBooleanValue(appConfigMap.get(AppConfigScopeEnum.PRODUCT_APP), getter);
            if (null == value) {
                value = fetchValidBooleanValue(appConfigMap.get(AppConfigScopeEnum.PRODUCT), getter);
            }
            if (null == value) {
                value = fetchValidBooleanValue(appConfigMap.get(AppConfigScopeEnum.APP), getter);
            }
            if (null == value && !excludeGlobalConfig) {
                value = fetchValidBooleanValue(appConfigMap.get(AppConfigScopeEnum.GLOBAL), getter);
            }
            return value;
        }

        public static Boolean fetchValidBooleanValue(AppConfig appConfig, Getter<AppConfig, Boolean> getter) {
            return Optional.ofNullable(appConfig).map(getter).orElse(null);
        }

    }

    @Override
    public AppConfig fetchGlobalConfig() {
        return fetchAppConfig(AppConfigScopeEnum.GLOBAL);
    }

    @Override
    public AppConfig fetchProductConfig(String product) {
        return fetchAppConfig(AppConfigScopeEnum.PRODUCT, product);
    }

    @Override
    public AppConfig fetchProductAppConfig(String product, String app) {
        return fetchAppConfig(AppConfigScopeEnum.PRODUCT_APP, product, app);
    }

    @Override
    public AppConfig fetchAppConfig(String app) {
        return fetchAppConfig(AppConfigScopeEnum.APP, app);
    }

    @Override
    public AppConfig fetchAppConfig(AppConfigScopeEnum scope, String... codes) {
        if (null == scope || !AppConfigScopeEnum.GLOBAL.equals(scope) && ArrayUtils.isEmpty(codes)) {
            throw PamirsException.construct(BASE_APP_CONFIG_PARAM_ERROR).errThrow();
        }

        String code = AppConfig.generateCode(scope, codes);
        String companyCode = (String) PamirsSession.getRequestInfo("companyCode");
        LambdaQueryWrapper<AppConfig> queryWrapper = Pops.<AppConfig>lambdaQuery()
                .from(AppConfig.MODEL_MODEL)
                .eq(AppConfig::getCode, code);
        if (companyCode == null) {
            queryWrapper.isNull(AppConfig::getCompanyCode);
        } else {
            queryWrapper.eq(AppConfig::getCompanyCode, companyCode);
        }
        return Models.data().queryOneByWrapper(queryWrapper);
    }

    @Override
    public List<AppConfig> fetchAppConfigList(List<String> apps) {
        LambdaQueryWrapper<AppConfig> queryWrapper = Pops.<AppConfig>lambdaQuery().from(AppConfig.MODEL_MODEL).setBatchSize(-1)
                .in(AppConfig::getApp, apps);
        String companyCode = PamirsSession.getTransmittableExtend().get("companyCode");
        queryWrapper.eq(StringUtils.isNotBlank(companyCode), AppConfig::getCompanyCode, companyCode);
        return new AppConfig().queryList(queryWrapper);
    }

    @Override
    public Map<String, AppConfig> fetchAppConfigMap(List<String> apps) {
        List<AppConfig> resultList = fetchAppConfigList(apps);
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        return resultList.stream().collect(Collectors.toMap(AppConfig::getApp, v -> v));
    }

    @Override
    public Map<AppConfigScopeEnum, AppConfig> fetchAppConfigMap(String product, String app) {
        boolean isApp = !StringUtils.isBlank(app);
        boolean isProduct = !StringUtils.isBlank(product);

        List<String> codes = new ArrayList<>();
        codes.add(AppConfig.generateCode(AppConfigScopeEnum.GLOBAL));
        if (isApp) {
            codes.add(AppConfig.generateCode(AppConfigScopeEnum.APP, app));
        }
        if (isProduct) {
            codes.add(AppConfig.generateCode(AppConfigScopeEnum.PRODUCT, product));
            if (isApp) {
                codes.add(AppConfig.generateCode(AppConfigScopeEnum.PRODUCT_APP, product, app));
            }
        }

        String companyCode = (String) PamirsSession.getRequestInfo("companyCode");
        LambdaQueryWrapper<AppConfig> queryWrapper = Pops.<AppConfig>lambdaQuery().from(AppConfig.MODEL_MODEL)
                .in(AppConfig::getCode, codes).setBatchSize(-1);
        queryWrapper.eq(StringUtils.isNotBlank(companyCode), AppConfig::getCompanyCode, companyCode);
        List<AppConfig> appConfigList = new AppConfig().queryList(queryWrapper);
        if (CollectionUtils.isEmpty(appConfigList)) {
            return null;
        }
        return appConfigList.stream().collect(Collectors.toMap(v -> {
            String scope = StringUtils.substringBefore(v.getCode(), CharacterConstants.SEPARATOR_OCTOTHORPE);
            return Enums.getEnumByValue(AppConfigScopeEnum.class, scope);
        }, v -> v));
    }

}
