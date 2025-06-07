package pro.shushi.pamirs.translate.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.boot.web.extend.AppConfigLoaderExtendApi;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.service.ResourceTranslationService;
import pro.shushi.pamirs.translate.constant.TranslateConstants;

import java.util.*;

/**
 * AppConfig翻译资源扩展
 *
 * @author Adamancy Zhang at 10:55 on 2024-06-21
 */
@Order(88)
@Component
public class AppConfigTranslationExtend implements AppConfigLoaderExtendApi {

    private static final String RESOURCE_TRANSLATIONS_KEY = "resourceTranslations";

    @Autowired
    private ResourceTranslationService resourceTranslationService;

    private static final String MODULE_FIELD = LambdaUtil.fetchFieldName(ResourceTranslation::getModule);

    private static final String MODULE_NAME_FIELD = LambdaUtil.fetchFieldName(ResourceTranslation::getModuleName);

    private static final String REMOTE_URL_FIELD = LambdaUtil.fetchFieldName(ResourceTranslation::getRemoteUrl);

    @Override
    public List<AppConfig> queryAfterProperties(List<AppConfig> appConfigs) {
        List<Map<String, Object>> resourceTranslations = queryResourceTranslations();
        if (CollectionUtils.isEmpty(resourceTranslations)) {
            return appConfigs;
        }
        AppConfig globalAppConfig = getOrAddGlobalAppConfig(appConfigs);
        Map<String, Object> extend = globalAppConfig.getExtend();
        extend.put(RESOURCE_TRANSLATIONS_KEY, resourceTranslations);
        return appConfigs;
    }

    private List<Map<String, Object>> queryResourceTranslations() {
        TranslateService translateService = TranslateServiceHolder.get();
        String lang = translateService.getCurrentLang();
        List<Map<String, Object>> resourceTranslationResults = null;

        if (translateService.needTranslate()) {
            boolean globalJs = Boolean.parseBoolean(FetchUtil.fetchVariables(TranslateConstants.TRANSLATION_ONLY_GLOBAL));
            List<ResourceTranslation> resourceTranslations = resourceTranslationService.queryListByWrapper(Pops.<ResourceTranslation>lambdaQuery()
                    .from(ResourceTranslation.MODEL_MODEL)
                    .select(ResourceTranslation::getId, ResourceTranslation::getModule, ResourceTranslation::getRemoteUrl)
                    .eq(ResourceTranslation::getResLangCode, DefaultResourceConstants.CHINESE_LANGUAGE_CODE)
                    .eq(globalJs, ResourceTranslation::getModule, TranslateConstants.COMMON)
                    .eq(ResourceTranslation::getLangCode, lang)
                    .eq(ResourceTranslation::getState, Boolean.TRUE));
            if (CollectionUtils.isEmpty(resourceTranslations)) {
                return null;
            }
            resourceTranslationResults = new ArrayList<>();
            for (ResourceTranslation resourceTranslation : resourceTranslations) {
                String remoteUrl = resourceTranslation.getRemoteUrl();
                if (StringUtils.isBlank(remoteUrl)) {
                    continue;
                }
                String module = resourceTranslation.getModule();
                String moduleName = Optional.ofNullable(module)
                        .filter(StringUtils::isNotBlank)
                        .map(v -> PamirsSession.getContext().getModule(v))
                        .map(ModuleDefinition::getName)
                        .orElse(null);
                if (StringUtils.isNotBlank(moduleName)) {
                    resourceTranslationResults.add(MapHelper.<String, Object>newInstance()
                            .put(MODULE_FIELD, module)
                            .put(MODULE_NAME_FIELD, moduleName)
                            .put(REMOTE_URL_FIELD, remoteUrl)
                            .build());
                }
            }
        }
        return resourceTranslationResults;
    }

    private AppConfig getOrAddGlobalAppConfig(List<AppConfig> appConfigs) {
        String code = AppConfig.generateCode(AppConfigScopeEnum.GLOBAL);
        AppConfig globalAppConfig = null;
        if (!appConfigs.isEmpty()) {
            for (AppConfig appConfig : appConfigs) {
                if (code.equals(appConfig.getCode())) {
                    globalAppConfig = appConfig;
                    break;
                }
            }
        }
        if (globalAppConfig == null) {
            globalAppConfig = new AppConfig();
            globalAppConfig.setCode(code);
            globalAppConfig.setScope(AppConfigScopeEnum.GLOBAL);
            appConfigs.add(globalAppConfig);
        }
        Map<String, Object> extend = globalAppConfig.getExtend();
        if (extend == null) {
            extend = new HashMap<>();
            globalAppConfig.setExtend(extend);
        }
        return globalAppConfig;
    }
}
