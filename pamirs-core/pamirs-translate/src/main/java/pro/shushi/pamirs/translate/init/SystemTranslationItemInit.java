package pro.shushi.pamirs.translate.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CommonModule;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.resource.api.enmu.TranslateDataSourcesEnum;
import pro.shushi.pamirs.resource.api.enmu.TranslationApplicationScopeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.constant.TranslateConstants;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 系统权限项初始化
 *
 * @author Adamancy Zhang at 12:29 on 2024-04-16
 */
@Slf4j
@Component
public class SystemTranslationItemInit {

    private static final String INIT_PATH = "classpath*:pamirs/init/translate/**/*.json";

    private static final String GLOBAL = "global";

    private static final TypeReference<Map<String, Object>> MAP_STRING_STRING = new TypeReference<Map<String, Object>>() {
    };

    /**
     * 读取resource下面的文件
     * <p>
     * /pamirs/init/translate/${global/module}/${resLangCode}/${langCode}/*.json
     */
    public void init() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(AppClassLoader.getClassLoader(TranslateDataInit.class));
        Resource[] resources;
        try {
            resources = resolver.getResources(INIT_PATH);
        } catch (IOException e) {
            log.error("translate init data resolve error.", e);
            return;
        }
        Map<String, Map<TranslationItemKey, ResourceTranslationItem>> moduleTranslateItemsMap = new HashMap<>(32);
        for (Resource resource : resources) {
            collectionTranslateItems(moduleTranslateItemsMap, resource);
        }
        fillModuleTranslateItems(moduleTranslateItemsMap);
        createNotExistItems(moduleTranslateItemsMap);
    }

    private void collectionTranslateItems(Map<String, Map<TranslationItemKey, ResourceTranslationItem>> moduleTranslateItemsMap, Resource resource) {
        Map<String, Object> map;
        try {
            String content = IOUtils.toString(resource.getInputStream(), String.valueOf(StandardCharsets.UTF_8));
            map = JSON.parseObject(content, MAP_STRING_STRING);
        } catch (Throwable e) {
            log.error("translate json parse error.", e);
            return;
        }
        if (MapUtils.isEmpty(map)) {
            return;
        }
        ResourceTranslationItem config = resolveResourcePath(resource);
        if (config == null) {
            return;
        }
        String module = config.getModule();
        String resLangCode = config.getResLangCode();
        String langCode = config.getLangCode();
        TranslationApplicationScopeEnum scope = config.getScope();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String origin = entry.getKey();
            if (StringUtils.isBlank(origin)) {
                continue;
            }
            Object targetObject = entry.getValue();
            if (!(targetObject instanceof String)) {
                log.error("Invalid translate item target. module: {}, origin: {}", module, origin);
                continue;
            }
            String target = (String) targetObject;
            if (StringUtils.isBlank(target)) {
                target = null;
            }
            addTranslationItem(moduleTranslateItemsMap, generatorTranslationItem(module, resLangCode, langCode, origin, target, scope, true));
        }
    }

    private ResourceTranslationItem resolveResourcePath(Resource resource) {
        String path;
        try {
            path = resource.getURL().getPath();
        } catch (IOException e) {
            log.error("translate get path error.", e);
            return null;
        }
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        String[] split = path.split("/");
        String langCode = split[split.length - 2];
        String resLangCode = split[split.length - 3];
        String module = split[split.length - 4];
        ResourceTranslationItem config = new ResourceTranslationItem();
        if (GLOBAL.equals(module)) {
            module = CommonModule.MODULE_MODULE;
            config.setScope(TranslationApplicationScopeEnum.GLOBAL);
        } else {
            config.setScope(TranslationApplicationScopeEnum.MODULE);
        }
        config.setModule(module);
        config.setLangCode(langCode);
        config.setResLangCode(resLangCode);
        return config;
    }

    private void fillModuleTranslateItems(Map<String, Map<TranslationItemKey, ResourceTranslationItem>> moduleTranslateItemsMap) {
        LambdaQueryWrapper<ModuleDefinition> qw = Pops.<ModuleDefinition>lambdaQuery()
                .from(ModuleDefinition.MODEL_MODEL)
                .select(ModuleDefinition::getModule, ModuleDefinition::getDisplayName)
                .eq(ModuleDefinition::getApplication, Boolean.TRUE);
        List<ModuleDefinition> moduleDefinitions = new ModuleDefinition().queryList(qw);
        if (CollectionUtils.isNotEmpty(moduleDefinitions)) {
            for (ModuleDefinition moduleDefinition : moduleDefinitions) {
                ResourceTranslationItem translationItem = generatorModuleTranslationItem(moduleDefinition);
                if (translationItem != null) {
                    addTranslationItem(moduleTranslateItemsMap, translationItem);
                }
            }
        }
    }

    private void createNotExistItems(Map<String, Map<TranslationItemKey, ResourceTranslationItem>> moduleTranslateItemsMap) {
        if (MapUtils.isEmpty(moduleTranslateItemsMap)) {
            return;
        }
        Map<String, ResourceTranslation> createTranslationMap = new HashMap<>(32);
        for (Map.Entry<String, Map<TranslationItemKey, ResourceTranslationItem>> entry : moduleTranslateItemsMap.entrySet()) {
            String module = entry.getKey();
            Map<TranslationItemKey, ResourceTranslationItem> createTranslationItems = entry.getValue();
            List<ResourceTranslationItem> existTranslationItems = DataShardingHelper.build().collectionSharding(createTranslationItems.keySet(), (sublist) -> {
                List<String> originCodes = new ArrayList<>();
                List<String> resLangCodes = new ArrayList<>();
                List<String> langCodes = new ArrayList<>();
                for (TranslationItemKey key : sublist) {
                    String resLangCode = key.getResLangCode();
                    String langCode = key.getLangCode();
                    String translationKey = StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE, module, resLangCode, langCode);
                    createTranslationMap.computeIfAbsent(translationKey, (k) -> {
                        ResourceTranslation resourceTranslation = new ResourceTranslation();
                        resourceTranslation.setModule(module);
                        resourceTranslation.setResLangCode(resLangCode);
                        resourceTranslation.setLangCode(langCode);
                        resourceTranslation.setState(Boolean.TRUE);
                        return resourceTranslation;
                    });
                    originCodes.add(key.getOriginCode());
                    resLangCodes.add(resLangCode);
                    langCodes.add(langCode);
                }
                return Models.origin().queryListByWrapper(Pops.<ResourceTranslationItem>lambdaQuery()
                        .from(ResourceTranslationItem.MODEL_MODEL)
                        .select(ResourceTranslationItem::getId, ResourceTranslationItem::getModule, ResourceTranslationItem::getOriginCode, ResourceTranslationItem::getResLangCode, ResourceTranslationItem::getLangCode)
                        .eq(ResourceTranslationItem::getModule, module)
                        .in(Arrays.asList(ResourceTranslationItem::getOriginCode, ResourceTranslationItem::getResLangCode, ResourceTranslationItem::getLangCode), originCodes, resLangCodes, langCodes)
                );
            });
            for (ResourceTranslationItem existTranslationItem : existTranslationItems) {
                createTranslationItems.remove(new TranslationItemKey(existTranslationItem));
            }
            if (!createTranslationItems.isEmpty()) {
                Models.origin().createBatch(new ArrayList<>(createTranslationItems.values()));
            }
        }
        if (!createTranslationMap.isEmpty()) {
            createNotExistTranslations(createTranslationMap);
        }
    }

    private void createNotExistTranslations(Map<String, ResourceTranslation> createTranslations) {
        List<ResourceTranslation> existTranslations = DataShardingHelper.build().collectionSharding(createTranslations.values(), (sublist) -> {
            List<String> modules = new ArrayList<>();
            List<String> resLangCodes = new ArrayList<>();
            List<String> langCodes = new ArrayList<>();
            for (ResourceTranslation translation : sublist) {
                modules.add(translation.getModule());
                resLangCodes.add(translation.getResLangCode());
                langCodes.add(translation.getLangCode());
            }
            return Models.origin().queryListByWrapper(Pops.<ResourceTranslation>lambdaQuery()
                    .from(ResourceTranslation.MODEL_MODEL)
                    .select(ResourceTranslation::getId, ResourceTranslation::getModule, ResourceTranslation::getResLangCode, ResourceTranslation::getLangCode)
                    .in(Arrays.asList(ResourceTranslation::getModule, ResourceTranslation::getResLangCode, ResourceTranslation::getLangCode), modules, resLangCodes, langCodes));
        });
        for (ResourceTranslation existTranslation : existTranslations) {
            String translationKey = StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE, existTranslation.getModule(), existTranslation.getResLangCode(), existTranslation.getLangCode());
            createTranslations.remove(translationKey);
        }
        if (!createTranslations.isEmpty()) {
            Models.origin().createBatch(new ArrayList<>(createTranslations.values()));
        }
    }

    private ResourceTranslationItem generatorModuleTranslationItem(ModuleDefinition moduleDefinition) {
        String displayName = moduleDefinition.getDisplayName();
        if (StringUtils.isBlank(displayName)) {
            return null;
        }
        return generatorTranslationItem(CommonModule.MODULE_MODULE,
                TranslateConstants.RES_LANG_CODE, TranslateConstants.LANG_CODE,
                moduleDefinition.getDisplayName(), null,
                TranslationApplicationScopeEnum.GLOBAL, false);
    }

    private ResourceTranslationItem generatorTranslationItem(String module,
                                                             String resLangCode, String langCode,
                                                             String origin, String target,
                                                             TranslationApplicationScopeEnum scope,
                                                             Boolean state) {
        ResourceTranslationItem resourceTranslationItem = new ResourceTranslationItem();
        resourceTranslationItem.setResLangCode(resLangCode);
        resourceTranslationItem.setLangCode(langCode);
        resourceTranslationItem.setModule(module);
        resourceTranslationItem.setOrigin(origin);
        resourceTranslationItem.setTarget(target);
        resourceTranslationItem.setDataSource(TranslateDataSourcesEnum.FILE_IMPORT_TRANSLATION);
        resourceTranslationItem.setScope(scope);
        resourceTranslationItem.setSystem(true);
        resourceTranslationItem.setState(state);
        resourceTranslationItem.initOriginCode();
        return resourceTranslationItem;
    }

    private void addTranslationItem(Map<String, Map<TranslationItemKey, ResourceTranslationItem>> moduleTranslateItemsMap, ResourceTranslationItem translationItem) {
        moduleTranslateItemsMap.computeIfAbsent(translationItem.getModule(), k -> new HashMap<>(32)).putIfAbsent(new TranslationItemKey(translationItem), translationItem);
    }

    private static final class TranslationItemKey implements Serializable {

        private static final long serialVersionUID = -7229729038789169058L;

        private final String originCode;

        private final String resLangCode;

        private final String langCode;

        public TranslationItemKey(ResourceTranslationItem translationItem) {
            this(translationItem.getOriginCode(), translationItem.getResLangCode(), translationItem.getLangCode());
        }

        public TranslationItemKey(String originCode, String resLangCode, String langCode) {
            this.originCode = originCode;
            this.resLangCode = resLangCode;
            this.langCode = langCode;
        }

        public String getOriginCode() {
            return originCode;
        }

        public String getResLangCode() {
            return resLangCode;
        }

        public String getLangCode() {
            return langCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TranslationItemKey)) {
                return false;
            }
            TranslationItemKey that = (TranslationItemKey) o;
            return Objects.equals(originCode, that.originCode) &&
                    Objects.equals(resLangCode, that.resLangCode) &&
                    Objects.equals(langCode, that.langCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(originCode, resLangCode, langCode);
        }
    }
}
