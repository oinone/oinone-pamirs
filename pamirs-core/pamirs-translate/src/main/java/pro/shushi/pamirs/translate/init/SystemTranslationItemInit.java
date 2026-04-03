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
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
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
        Map<String, Map<String, ResourceTranslationItem>> moduleTranslateItemsMap = new HashMap<>(32);
        for (Resource resource : resources) {
            collectionTranslateItems(moduleTranslateItemsMap, resource);
        }
        if (moduleTranslateItemsMap.isEmpty()) {
            return;
        }
        fillModuleTranslateItems(moduleTranslateItemsMap);
        createNotExistItems(moduleTranslateItemsMap);
    }

    private void collectionTranslateItems(Map<String, Map<String, ResourceTranslationItem>> moduleTranslateItemsMap, Resource resource) {
        Map<String, Object> map;
        try {
            String content = IOUtils.toString(resource.getInputStream(), String.valueOf(StandardCharsets.UTF_8));
            map = JSON.parseObject(content, MAP_STRING_STRING);
        } catch (Throwable e) {
            log.error("translate json parse error.", e);
            return;
        }
        try {
            log.info("read translate json: {}", resource.getURL());
        } catch (Throwable ignored) {
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

    private void fillModuleTranslateItems(Map<String, Map<String, ResourceTranslationItem>> moduleTranslateItemsMap) {
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

    private void createNotExistItems(Map<String, Map<String, ResourceTranslationItem>> moduleTranslateItemsMap) {
        List<ResourceTranslationItem> existTranslationItems = Models.origin().queryListByWrapper(Pops.<ResourceTranslationItem>lambdaQuery()
                .from(ResourceTranslationItem.MODEL_MODEL)
                .select(ResourceTranslationItem::getId,
                        ResourceTranslationItem::getModule,
                        ResourceTranslationItem::getScope,
                        ResourceTranslationItem::getResLangCode,
                        ResourceTranslationItem::getLangCode,
                        ResourceTranslationItem::getOriginCode)
                .in(ResourceTranslationItem::getModule, new HashSet<>(moduleTranslateItemsMap.keySet())));
        MemoryListSearchCache<String, ResourceTranslationItem> cache = new MemoryListSearchCache<>(existTranslationItems, this::generatorUniqueKey);
        Map<String, ResourceTranslation> createTranslationMap = new HashMap<>(32);
        List<ResourceTranslationItem> createTranslationItems = new ArrayList<>();
        for (Map.Entry<String, Map<String, ResourceTranslationItem>> entry : moduleTranslateItemsMap.entrySet()) {
            String module = entry.getKey();
            Map<String, ResourceTranslationItem> translationItems = entry.getValue();
            for (ResourceTranslationItem translateItem : translationItems.values()) {
                if (cache.get(generatorUniqueKey(translateItem)) == null) {
                    createTranslationItems.add(translateItem);
                }
            }
            for (ResourceTranslationItem translateItem : translationItems.values()) {
                String resLangCode = translateItem.getResLangCode();
                String langCode = translateItem.getLangCode();
                String key = StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE, module, resLangCode, langCode);
                if (!createTranslationMap.containsKey(key)) {
                    ResourceTranslation resourceTranslation = new ResourceTranslation();
                    resourceTranslation.setModule(module);
                    resourceTranslation.setResLangCode(resLangCode);
                    resourceTranslation.setLangCode(langCode);
                    resourceTranslation.setState(Boolean.TRUE);
                    createTranslationMap.put(key, resourceTranslation);
                }
            }
        }
        if (!createTranslationMap.isEmpty()) {
            createNotExistTranslations(moduleTranslateItemsMap.keySet(), createTranslationMap.values());
        }
        if (!createTranslationItems.isEmpty()) {
            Models.origin().createBatch(createTranslationItems);
        }
    }

    private void createNotExistTranslations(Set<String> modules, Collection<ResourceTranslation> translations) {
        List<ResourceTranslation> existTranslations = Models.origin().queryListByWrapper(Pops.<ResourceTranslation>lambdaQuery()
                .from(ResourceTranslation.MODEL_MODEL)
                .select(ResourceTranslation::getId, ResourceTranslation::getModule, ResourceTranslation::getResLangCode, ResourceTranslation::getLangCode)
                .in(ResourceTranslation::getModule, new ArrayList<>(modules)));
        MemoryListSearchCache<String, ResourceTranslation> cache = new MemoryListSearchCache<>(existTranslations, this::generatorUniqueKey);
        List<ResourceTranslation> createTranslates = new ArrayList<>();
        for (ResourceTranslation translate : translations) {
            if (cache.get(this.generatorUniqueKey(translate)) == null) {
                createTranslates.add(translate);
            }
        }
        if (!createTranslates.isEmpty()) {
            Models.origin().createBatch(createTranslates);
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

    private void addTranslationItem(Map<String, Map<String, ResourceTranslationItem>> moduleTranslateItemsMap, ResourceTranslationItem translationItem) {
        moduleTranslateItemsMap.computeIfAbsent(translationItem.getModule(), k -> new HashMap<>(32))
                .putIfAbsent(generatorUniqueKey(translationItem), translationItem);
    }

    private String generatorUniqueKey(ResourceTranslation translation) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                translation.getModule(),
                translation.getResLangCode(),
                translation.getLangCode()
        );
    }

    private String generatorUniqueKey(ResourceTranslationItem translationItem) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                translationItem.getScope().getValue(),
                translationItem.getModule(),
                translationItem.getResLangCode(),
                translationItem.getLangCode(),
                translationItem.getOriginCode()
        );
    }
}
