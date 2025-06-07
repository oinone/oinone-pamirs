package pro.shushi.pamirs.translate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.api.client.util.Charsets;
import com.google.common.hash.Hashing;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.resource.api.enmu.TranslateDataSourcesEnum;
import pro.shushi.pamirs.resource.api.enmu.TranslationApplicationScopeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.constant.TranslateConstants;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.entity.TranslationLanguage;
import pro.shushi.pamirs.translate.manager.base.TranslateModuleManager;
import pro.shushi.pamirs.translate.proxy.TranslationItemProxy;
import pro.shushi.pamirs.translate.service.TranslateCompileContext;
import pro.shushi.pamirs.translate.service.TranslationItemPageNodeVisitor;
import pro.shushi.pamirs.translate.service.TranslationItemProxyService;
import pro.shushi.pamirs.translate.visitor.DslParser;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.translate.constant.TranslateConstants.FIELD_TO_EXCLUDE;
import static pro.shushi.pamirs.translate.constant.TranslateConstants.MODULES_TO_EXCLUDE;
import static pro.shushi.pamirs.translate.enmu.TranslateEnumerate.TRANSLATION_APPLICATION_CONFLICT;
import static pro.shushi.pamirs.translate.enmu.TranslateEnumerate.UNKNOWN_LANG_ERROR;

/**
 * @author: xuxin
 * @createTime: 2024/05/12 10:48
 */
@Service
@Slf4j
public class TranslationItemProxyServiceImpl implements TranslationItemProxyService {
    private final static ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(2));
    @Autowired
    private MetaCacheManager metaCacheManager;

    @Override
    public void create(ResourceTranslation data) {
        String module = data.getModule();
        data.setState(Boolean.TRUE);
        List<ResourceTranslationItem> translationItems = data.getTranslationItems();
        List<ResourceTranslationItem> globalResourceTranslationItems = new ArrayList<>();

        //module,originCode,resLangCode,langCode,scope
        HashMap<String, ResourceTranslationItem> existItem = new HashMap<>();
        for (ResourceTranslationItem item : translationItems) {
            TranslationApplicationScopeEnum scope = item.getScope();
            if (scope == null) {
                scope = TranslationApplicationScopeEnum.MODULE;
            }
            item.setLangCode(data.getLangCode());
            item.setResLangCode(data.getResLangCode());
            item.setModule(module);
            item.setComments(data.getComments());
            item.setDataSource(TranslateDataSourcesEnum.BATCH_TRANSLATION);
            item.setScope(scope);
            item.initOriginCode();

            String itemUnique = String.join(",", module, item.getOriginCode(), item.getResLangCode(), item.getLangCode(), scope.getValue());
            if (existItem.containsKey(itemUnique)) {
                existItem.put(itemUnique, item);
                continue;
            }
            if (TranslationApplicationScopeEnum.GLOBAL.equals(item.getScope())) {
                globalResourceTranslationItems.add(item);
            }
            existItem.put(itemUnique, item);
        }

        data.createOrUpdate();
        if (CollectionUtils.isNotEmpty(globalResourceTranslationItems)) {
            String error = verifyGlobalTranslationItemData(globalResourceTranslationItems, data);
            if (StringUtils.isNotBlank(error)) {
                throw PamirsException.construct(TRANSLATION_APPLICATION_CONFLICT, error).errThrow();
            }
        }
        Collection<ResourceTranslationItem> values = existItem.values();
        if (CollectionUtils.isNotEmpty(values)) {
            new ResourceTranslationItem().createOrUpdateBatch(new ArrayList<>(values));
        }
    }

    @Override
    public void createOrUpdateAndRefreshBatch(ResourceTranslation data, String resLangCode) {
        List<ResourceTranslationItem> translationItems = data.getTranslationItems();
        List<ResourceTranslationItem> globalResourceTranslationItems = new ArrayList<>();

        String finalModule = data.getModule();
        data.setState(Boolean.TRUE);
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            HashMap<String, ResourceTranslationItem> existItem = new HashMap<>();
            for (ResourceTranslationItem item : translationItems) {
                TranslationApplicationScopeEnum scope = item.getScope();
                if (scope == null) {
                    scope = TranslationApplicationScopeEnum.MODULE;
                }
                item.setLangCode(data.getLangCode());
                item.setResLangCode(data.getResLangCode());
                item.setModule(finalModule);
                item.setDataSource(TranslateDataSourcesEnum.PAGE_ADD);
                item.initOriginCode();

                String itemUnique = String.join(",", finalModule, item.getOriginCode(), item.getResLangCode(), item.getLangCode(), scope.getValue());
                if (existItem.containsKey(itemUnique)) {
                    continue;
                }
                if (TranslationApplicationScopeEnum.GLOBAL.equals(item.getScope())) {
                    globalResourceTranslationItems.add(item);
                }
                existItem.put(itemUnique, item);
            }
            data.createOrUpdate();
            if (CollectionUtils.isNotEmpty(globalResourceTranslationItems)) {
                String error = verifyGlobalTranslationItemData(globalResourceTranslationItems, data);
                if (StringUtils.isNotBlank(error)) {
                    throw PamirsException.construct(TRANSLATION_APPLICATION_CONFLICT, error).errThrow();
                }
            }
            if (CollectionUtils.isNotEmpty(translationItems)) {
                new ResourceTranslationItem().createOrUpdateBatch(translationItems);
            }
        }, executorService);

        try {
            // 等待异步任务完成
            completableFuture.join();
        } catch (CompletionException e) {
            throw PamirsException.construct(TRANSLATION_APPLICATION_CONFLICT, e.getCause().getMessage()).errThrow();
        }
        //------- 刷新远程资源-------
        refreshRemoteResource(data, translationItems, globalResourceTranslationItems, resLangCode);
    }

    @Override
    public void refreshRemoteResource() {
        // 刷新前端翻译项
        translationResourceAnalysis();
    }

    @Override
    public List<TranslationItemProxy> queryByInsertPage(TranslationItemProxy data, String langCode, String resLangCode, String searchOrigin) {
        String module = data.getModule();
        CompletableFuture<Set<String>> originFuture = parseXmlModelEnumAsync(data, module);
        CompletableFuture<Set<String>> systemFuture = fetchGlobalAsyncTranslatedValue(langCode, resLangCode, module);
        //TODO 源语言编码写到YML中
        CompletableFuture<Map<String, TranslationItemProxy>> moduleFuture = fetchAsyncTranslatedValueForApp(langCode, resLangCode, module);

        Set<String> originContext = originFuture.join();
        Map<String, TranslationItemProxy> origins = moduleFuture.join();
        Set<String> systemOrigins = systemFuture.join();

        if (!(systemOrigins.isEmpty())) {
            originContext.addAll(systemOrigins);
        }

        if (CollectionUtils.isNotEmpty(originContext) && !(origins.isEmpty())) {
            originContext.removeAll(origins.keySet());
        }
        if (CollectionUtils.isNotEmpty(originContext)) {
            Set<Object> originCodes = originContext.stream().map(item -> Hashing.md5().hashString(item, Charsets.UTF_8).toString()).collect(Collectors.toSet());

            LambdaQueryWrapper<TranslationItemProxy> qw = new LambdaQueryWrapper<TranslationItemProxy>()
                    .from(TranslationItemProxy.MODEL_MODEL)
                    .eq(TranslationItemProxy::getLangCode, langCode)
                    .eq(TranslationItemProxy::getResLangCode, resLangCode)
                    .eq(TranslationItemProxy::getScope, TranslationApplicationScopeEnum.GLOBAL)
                    .isNotNull(TranslationItemProxy::getTarget)
                    .in(TranslationItemProxy::getOriginCode, originCodes);

            List<TranslationItemProxy> dbItemList = new TranslationItemProxy().queryList(qw);
            Map<String, TranslationItemProxy> globalOrigins = dbItemList.stream().collect(Collectors.toMap(TranslationItemProxy::getOrigin, java.util.function.Function.identity()));
            if (!(globalOrigins.isEmpty())) {
                originContext.removeAll(globalOrigins.keySet());
            }
        }
        //生成未翻译的字段
        List<TranslationItemProxy> translationItemExportProxies = getTranslationItemExportProxies(langCode, module, resLangCode, originContext, searchOrigin);
        if (CollectionUtils.isNotEmpty(translationItemExportProxies)) {
            new TranslationItemProxy().listFieldQuery(translationItemExportProxies, TranslationItemProxy::getLang);
            new TranslationItemProxy().listFieldQuery(translationItemExportProxies, TranslationItemProxy::getResLang);
            new TranslationItemProxy().listFieldQuery(translationItemExportProxies, TranslationItemProxy::getModuleDefinition);
        }
        return translationItemExportProxies;
    }


    @Override
    public List<TranslationItemProxy> queryByChangePage(TranslationItemProxy data, String langCode, String resLangCode, String searchTarget) {
        String module = data.getModule();
        ArrayList<TranslationItemProxy> result = new ArrayList<>();
        CompletableFuture<Set<String>> originFuture = parseXmlModelEnumAsync(data, module);
        CompletableFuture<Set<String>> systemFuture = fetchGlobalAsyncTranslatedValue(langCode, resLangCode, module);
        //TODO 源语言编码写到YML中
        CompletableFuture<Map<String, TranslationItemProxy>> moduleFuture = fetchAsyncTranslatedValueForApp(langCode, resLangCode, module);

        Map<String, TranslationItemProxy> globalTranslationItemProxyMap = new HashMap<>();
        Set<String> originContext = originFuture.join();
        Map<String, TranslationItemProxy> translationItemProxyMap = moduleFuture.join();
        Set<String> systemOrigins = systemFuture.join();

        if (!(systemOrigins.isEmpty())) {
            originContext.addAll(systemOrigins);
        }

        if (CollectionUtils.isNotEmpty(originContext) && !(translationItemProxyMap.isEmpty())) {
            originContext.removeAll(translationItemProxyMap.keySet());
        }
        if (CollectionUtils.isNotEmpty(originContext)) {
            Set<Object> originCodes = originContext.stream().map(item -> Hashing.md5().hashString(item, Charsets.UTF_8).toString()).collect(Collectors.toSet());

            LambdaQueryWrapper<TranslationItemProxy> qw = new LambdaQueryWrapper<TranslationItemProxy>()
                    .from(TranslationItemProxy.MODEL_MODEL)
                    .eq(TranslationItemProxy::getLangCode, langCode)
                    .eq(TranslationItemProxy::getResLangCode, resLangCode)
                    .eq(TranslationItemProxy::getScope, TranslationApplicationScopeEnum.GLOBAL)
                    .isNotNull(TranslationItemProxy::getTarget)
                    .in(TranslationItemProxy::getOriginCode, originCodes);

            List<TranslationItemProxy> dbItemList = new TranslationItemProxy().queryList(qw);
            if (CollectionUtils.isNotEmpty(dbItemList)) {
                globalTranslationItemProxyMap = dbItemList.stream()
                        .peek(item -> item.setModule(module))
                        .collect(Collectors.toMap(TranslationItemProxy::getOrigin, java.util.function.Function.identity()));
            }

            if (!(globalTranslationItemProxyMap.isEmpty())) {
                result.addAll(globalTranslationItemProxyMap.values());
            }
        }
        if (!(translationItemProxyMap.isEmpty())) {
            result.addAll(translationItemProxyMap.values());
        }

        if (translationItemProxyMap.isEmpty() && globalTranslationItemProxyMap.isEmpty()) {
            return result;
        }

        if (StringUtils.isNotBlank(searchTarget)) {
            for (String key : translationItemProxyMap.keySet()) {
                if (key.contains(searchTarget)) {
                    result.add(translationItemProxyMap.get(key));
                }
            }
            for (String key : globalTranslationItemProxyMap.keySet()) {
                if (key.contains(searchTarget)) {
                    result.add(globalTranslationItemProxyMap.get(key));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(result)) {
            new TranslationItemProxy().listFieldQuery(result, TranslationItemProxy::getLang);
            new TranslationItemProxy().listFieldQuery(result, TranslationItemProxy::getResLang);
            new TranslationItemProxy().listFieldQuery(result, TranslationItemProxy::getModuleDefinition);
        }
        return result;
    }

    @Override
    public void createTranslationResourceItem(TranslationItemProxy data, String resLangCode) {
        String module = data.getModule();
        ResourceTranslation existByUnique = new ResourceTranslation();
        existByUnique.setModule(module);
        existByUnique.setResLangCode(resLangCode);
        existByUnique.setLangCode(data.getLangCode());
        existByUnique = existByUnique.queryOne();
        data.setDataSource(TranslateDataSourcesEnum.PAGE_ADD);

        if (data.getScope() == null) {
            data.setScope(TranslationApplicationScopeEnum.MODULE);
        }
        data.initOriginCode();

        if (TranslationApplicationScopeEnum.GLOBAL.equals(data.getScope())) {
            LambdaQueryWrapper<ResourceTranslationItem> qw = Pops.<ResourceTranslationItem>lambdaQuery()
                    .from(ResourceTranslationItem.MODEL_MODEL)
                    .eq(ResourceTranslationItem::getOriginCode, data.getOriginCode())
                    .eq(ResourceTranslationItem::getResLangCode, resLangCode)
                    .eq(ResourceTranslationItem::getScope, TranslationApplicationScopeEnum.GLOBAL)
                    .eq(ResourceTranslationItem::getLangCode, data.getLangCode())
                    .isNotNull(ResourceTranslationItem::getTarget);

            ResourceTranslationItem globalResourceTranslationItem = new ResourceTranslationItem().queryOneByWrapper(qw);
            if (globalResourceTranslationItem != null && !module.equals(globalResourceTranslationItem.getModule())) {
                throw PamirsException.construct(TranslateEnumerate.SOURCE_TERM_ALREADY_EXISTS_IN_ANOTHER_MODULE, globalResourceTranslationItem.getModule(), data.getOrigin()).errThrow();
            }
        }
        if (existByUnique == null) {
            ResourceTranslation resourceTranslation = new ResourceTranslation();
            resourceTranslation.setModule(module);
            resourceTranslation.setState(Boolean.TRUE);
            resourceTranslation.setResLangCode(resLangCode);
            resourceTranslation.setLangCode(data.getLangCode());
            resourceTranslation.create();
        }
        data.createOrUpdate();
    }

    @Override
    public void updateTranslationResourceItem(TranslationItemProxy data, String resLangCode) {
        data.initOriginCode();
        data.setDataSource(TranslateDataSourcesEnum.PAGE_ADD);
        ResourceTranslation existByUnique = new ResourceTranslation();
        existByUnique.setModule(data.getModule());
        existByUnique.setResLangCode(data.getResLangCode());
        existByUnique.setLangCode(data.getLangCode());
        existByUnique = existByUnique.queryOne();

        if (TranslationApplicationScopeEnum.GLOBAL.equals(data.getScope())) {
            LambdaQueryWrapper<ResourceTranslationItem> qw = Pops.<ResourceTranslationItem>lambdaQuery()
                    .from(ResourceTranslationItem.MODEL_MODEL)
                    .eq(ResourceTranslationItem::getOriginCode, data.getOriginCode())
                    .eq(ResourceTranslationItem::getResLangCode, resLangCode)
                    .eq(ResourceTranslationItem::getScope, TranslationApplicationScopeEnum.GLOBAL)
                    .eq(ResourceTranslationItem::getLangCode, data.getLangCode())
                    .isNotNull(ResourceTranslationItem::getTarget);

            ResourceTranslationItem globalResourceTranslationItem = new ResourceTranslationItem().queryOneByWrapper(qw);
            if (globalResourceTranslationItem != null && !data.getModule().equals(globalResourceTranslationItem.getModule())) {
                throw PamirsException.construct(TranslateEnumerate.SOURCE_TERM_ALREADY_EXISTS_IN_ANOTHER_MODULE, PamirsSession.getContext().getModule(globalResourceTranslationItem.getModule()).getDisplayName(), data.getOrigin()).errThrow();
            }
        }
        if (existByUnique == null) {
            ResourceTranslation resourceTranslation = new ResourceTranslation();
            resourceTranslation.setModule(data.getModule());
            resourceTranslation.setState(Boolean.TRUE);
            resourceTranslation.setResLangCode(resLangCode);
            resourceTranslation.setLangCode(data.getLangCode());
            resourceTranslation.create();
        }
        data.createOrUpdate();
    }

    //异步解析 XML/模型/枚举
    private CompletableFuture<Set<String>> parseXmlModelEnumAsync(TranslationItemProxy data, String module) {
        CompletableFuture<Set<String>> originFuture = CompletableFuture.supplyAsync(() -> {
            Set<String> originContext = new HashSet<>();
            if (!(MODULES_TO_EXCLUDE.contains(module))) {
                TranslationItemPageNodeVisitor translationItemPageNodeVisitor = getTranslationDslNodeVisitor(data.getModel(), data.getAction(), module);
                originContext = translationItemPageNodeVisitor.context;
                //解析模型
                parseModel(originContext, module);
                //解析枚举
                parseModelEnum(originContext, module);
            }
            return originContext;
        }, TtlAsyncTaskExecutor.getExecutorService());
        return originFuture;
    }

    private void translationResourceAnalysis() {
        FileClient fileClient = FileClientFactory.getClient();
        if (fileClient == null) {
            throw PamirsException.construct(TranslateEnumerate.NOTFOUND_FILE_CLIEND_CONFIG).errThrow();
        }
        /* moduleName,  language,   key,   value */
        Map<String, Map<TranslationLanguage, Map<String, String>>> moduleMap = new ConcurrentHashMap<>(64);
        List<ResourceLang> resourceLangs = new ResourceLang().queryList();
        if (CollectionUtils.isEmpty(resourceLangs)) {
            return;
        }
        Map<String, String> languageCodeMap = resourceLangs.stream()
                .filter(item -> item != null && StringUtils.isNotBlank(item.getCode()) && StringUtils.isNotBlank(item.getIsoCode()))
                .collect(Collectors.toMap(ResourceLang::getCode, ResourceLang::getIsoCode, (existingValue, newValue) -> existingValue));

        IWrapper<ResourceTranslationItem> itemQw = Pops.<ResourceTranslationItem>lambdaQuery()
                .from(ResourceTranslationItem.MODEL_MODEL)
                .isNotNull(ResourceTranslationItem::getTarget)
                .gt(ResourceTranslationItem::getId, 0);

        ResourceTranslationItem query = new ResourceTranslationItem();
        long count = query.count(itemQw);
        int total = (int) Math.ceil((double) count / 1000); // 计算总页数

        TranslateModuleManager translateModuleManager = new TranslateModuleManager(Boolean.FALSE);
        /* language,   key,   value */
        Map<TranslationLanguage, Map<String, String>> globalTranslationMap = new ConcurrentHashMap<>();
        CountDownLatch countDown = new CountDownLatch(total);
        Map<String, String> moduleNameMap = translateModuleManager.getModuleNameMap();
        for (int i = 1; i <= total; i++) {
            int finalI = i;
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    Pagination<ResourceTranslationItem> page = new Pagination<>();
                    page.setCurrentPage(finalI);
                    page.setSize(1000L);
                    List<ResourceTranslationItem> list = Models.data().queryListByWrapper(page, itemQw);
                    for (ResourceTranslationItem translation : Optional.ofNullable(list).orElse(Collections.emptyList())) {
                        String moduleName = moduleNameMap.get(translation.getModule());
                        if (StringUtils.isBlank(moduleName)) {
                            continue;
                        }
                        String targetLanguageCode = translation.getLangCode();
                        if (StringUtils.isBlank(targetLanguageCode)) {
                            continue;
                        }
                        Boolean state = translation.getState();
                        if (Boolean.FALSE.equals(state)) {
                            continue;
                        }
                        String targetLanguageISOCode = Optional.ofNullable(languageCodeMap.get(targetLanguageCode))
                                .orElseThrow(() -> PamirsException.construct(TranslateEnumerate.UNKNOWN_LANG_ERROR).errThrow());

                        TranslationLanguage language = new TranslationLanguage(targetLanguageCode, targetLanguageISOCode);
                        if (TranslationApplicationScopeEnum.GLOBAL.equals(translation.getScope())) {
                            globalTranslationMap.computeIfAbsent(language, k -> new ConcurrentHashMap<>())
                                    .put(translation.getOrigin(), translation.getTarget());
                            continue;
                        }

                        moduleMap.computeIfAbsent(moduleName, k -> new ConcurrentHashMap<>(256))
                                .computeIfAbsent(language, k -> new ConcurrentHashMap<>())
                                .put(translation.getOrigin(), translation.getTarget());
                    }
                } catch (Exception e) {
                    log.error("刷新远程翻译资源异常", e);
                } finally {
                    countDown.countDown();
                }
            });
        }

        try {
            countDown.await();
        } catch (InterruptedException e) {
            log.error("刷新远程翻译资源异常", e);
        }

        IWrapper<ResourceTranslation> translationWrapper = Pops.<ResourceTranslation>lambdaQuery()
                .from(ResourceTranslation.MODEL_MODEL)
                .isNotNull(ResourceTranslation::getRemoteUrl)
                .gt(ResourceTranslation::getId, 0);
        List<ResourceTranslation> resourceTranslations = Models.origin().queryListByWrapper(translationWrapper);
        Map<String, Map<TranslationLanguage, ResourceTranslation>> moduleTranslateionMap = new ConcurrentHashMap<>(64);
        if (CollectionUtils.isNotEmpty(resourceTranslations)) {
            for (ResourceTranslation resourceTranslation : resourceTranslations) {
                String langCode = resourceTranslation.getLangCode();
                String targetLanguageISOCode = Optional.ofNullable(languageCodeMap.get(langCode)).orElseThrow(() -> PamirsException.construct(TranslateEnumerate.UNKNOWN_LANG_ERROR).errThrow());
                TranslationLanguage language = new TranslationLanguage(langCode, targetLanguageISOCode);
                moduleTranslateionMap.computeIfAbsent(moduleNameMap.get(resourceTranslation.getModule()), k -> new ConcurrentHashMap<>(256)).putIfAbsent(language, resourceTranslation);
            }
        }

        Set<Object> updateResourceTranslations = new CopyOnWriteArraySet<>();

        for (Map.Entry<TranslationLanguage, Map<String, String>> entry : globalTranslationMap.entrySet()) {
            TranslationLanguage language = entry.getKey();
            String languageCode = language.getLangCode();
            String isoCode = language.getIsoCode();
            Map<String, String> globalTranslation = entry.getValue();
            ResourceTranslation updateResourceTranslation = new ResourceTranslation();
            updateResourceTranslation.setModule(TranslateConstants.COMMON);
            updateResourceTranslation.setResLangCode(TranslateConstants.RES_LANG_CODE);
            updateResourceTranslation.setLangCode(languageCode);
            updateResourceTranslation.setRemoteUrl(upload(fileClient, generatorPath(TranslateConstants.TRANSLATE_GLOBAL_PATH), isoCode, JSON.toJSONString(globalTranslation)));
            updateResourceTranslations.add(updateResourceTranslation);
        }

        Map<String, String> moduleNameModuleMap = translateModuleManager.getModuleNameModuleMap();
        for (Map.Entry<String, String> moduleNameEntity : moduleNameModuleMap.entrySet()) {
            String moduleName = moduleNameEntity.getKey();
            Map<TranslationLanguage, Map<String, String>> languageMap = moduleMap.get(moduleName);

            //清除数据
            if (MapUtils.isEmpty(languageMap)) {
                if (!(TranslateConstants.PUBLIC_RESOURCE.contains(moduleName))) {
                    Map<TranslationLanguage, ResourceTranslation> resourceTranslationMap = moduleTranslateionMap.get(moduleName);
                    if (MapUtils.isEmpty(resourceTranslationMap)) {
                        continue;
                    }
                    List<CompletableFuture<Void>> futureList = resourceTranslationMap.values().stream().map(resourceTranslation -> {
                        return CompletableFuture.runAsync(() -> {
                            resourceTranslation.setRemoteUrl(null);
                            updateResourceTranslations.add(resourceTranslation);
                        }, TtlAsyncTaskExecutor.getExecutorService());
                    }).collect(Collectors.toList());
                    futureList.forEach(CompletableFuture::join);
                }
            } else {
                //异步刷新远程资源
                List<CompletableFuture<Void>> futureList = languageMap.entrySet().stream().map(languageEntry -> {
                    TranslationLanguage language = languageEntry.getKey();
                    String languageCode = language.getLangCode();
                    String isoCode = language.getIsoCode();
                    Map<String, String> lmap = languageEntry.getValue();
                    return CompletableFuture.runAsync(() -> {
                        if (!(TranslateConstants.PUBLIC_RESOURCE.contains(moduleName))) {
                            try {
                                Map<TranslationLanguage, ResourceTranslation> resourceTranslationMap = moduleTranslateionMap.get(moduleName);
                                if (MapUtils.isNotEmpty(resourceTranslationMap)) {
                                    resourceTranslationMap.remove(language);
                                }
                                String module = moduleNameModuleMap.get(moduleName);
                                if (StringUtils.isBlank(module)) {
                                    log.error("Invalid module name. {}", moduleName);
                                } else {
                                    ResourceTranslation updateResourceTranslation = new ResourceTranslation();
                                    updateResourceTranslation.setModule(module);
                                    updateResourceTranslation.setResLangCode(TranslateConstants.RES_LANG_CODE);
                                    updateResourceTranslation.setLangCode(languageCode);
                                    updateResourceTranslation.setRemoteUrl(upload(fileClient, generatorPath(moduleName), isoCode, JSON.toJSONString(lmap)));
                                    updateResourceTranslations.add(updateResourceTranslation);
                                    if (MapUtils.isNotEmpty(resourceTranslationMap)) {
                                        for (Map.Entry<TranslationLanguage, ResourceTranslation> resourceTranslationEntry : resourceTranslationMap.entrySet()) {
                                            TranslationLanguage translationEntryKey = resourceTranslationEntry.getKey();
                                            if (!updateResourceTranslations.contains(translationEntryKey)) {
                                                ResourceTranslation resourceTranslation = resourceTranslationEntry.getValue();
                                                resourceTranslation.setRemoteUrl(null);
                                                updateResourceTranslations.add(resourceTranslation);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.error("刷新远程翻译资源出错", e);
                            }
                        }
                    }, TtlAsyncTaskExecutor.getExecutorService());
                }).collect(Collectors.toList());
                futureList.forEach(CompletableFuture::join);
            }

        }
        if (!updateResourceTranslations.isEmpty()) {
            Models.origin().updateBatch(new ArrayList<>(updateResourceTranslations));
        }
    }


    private String verifyGlobalTranslationItemData(List<ResourceTranslationItem> globalResourceTranslationItems, ResourceTranslation data) {
        StringBuilder errorBuilder = new StringBuilder();
        List<String> originCodes = globalResourceTranslationItems.stream().map(ResourceTranslationItem::getOriginCode).collect(Collectors.toList());
        LambdaQueryWrapper<ResourceTranslationItem> qw = Pops.<ResourceTranslationItem>lambdaQuery()
                .from(ResourceTranslationItem.MODEL_MODEL)
                .eq(ResourceTranslationItem::getScope, TranslationApplicationScopeEnum.GLOBAL)
                .in(ResourceTranslationItem::getOriginCode, originCodes)
                .eq(ResourceTranslationItem::getResLangCode, data.getResLangCode())
                .eq(ResourceTranslationItem::getLangCode, data.getLangCode())
                .isNotNull(ResourceTranslationItem::getTarget);

        List<ResourceTranslationItem> translationItemlistDb = new ResourceTranslationItem().queryList(qw);
        if (CollectionUtils.isNotEmpty(translationItemlistDb)) {
            translationItemlistDb.stream().filter(item -> !data.getModule().equals(item.getModule()))
                    .map(item -> {
                        ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(item.getModule());
                        return errorBuilder.append(" 已存在应用为: ")
                                .append(moduleDefinition.getDisplayName())
                                .append(" ,源术语为 ").append(item.getOrigin())
                                .append(" 的全局翻译项. ");
                    })
                    .collect(Collectors.toList());
        }
        return errorBuilder.toString();
    }

    private static void refreshRemoteResource(ResourceTranslation data, List<ResourceTranslationItem> translationItems, List<ResourceTranslationItem> globalResourceTranslationItems, String resLangCode) {
        FileClient fileClient = FileClientFactory.getClient();
        if (fileClient == null) {
            throw PamirsException.construct(TranslateEnumerate.NOTFOUND_FILE_CLIEND_CONFIG).errThrow();
        }
        if (CollectionUtils.isNotEmpty(translationItems) && CollectionUtils.isNotEmpty(globalResourceTranslationItems)) {
            queryTranslationGlobal(data, resLangCode, fileClient);
            queryTranslationModule(data, resLangCode, fileClient);
        } else if (CollectionUtils.isNotEmpty(globalResourceTranslationItems)) {
            queryTranslationGlobal(data, resLangCode, fileClient);
        } else {
            queryTranslationModule(data, resLangCode, fileClient);
        }
    }


    /**
     * 查询全局所属应用的翻译值
     *
     * @param data
     * @param resLangCode
     * @param fileClient
     */
    private static void queryTranslationGlobal(ResourceTranslation data, String resLangCode, FileClient fileClient) {
        Map<String, String> maps = new ConcurrentHashMap<>();
        LambdaQueryWrapper<ResourceTranslationItem> queryWrapper = Pops.<ResourceTranslationItem>lambdaQuery()
                .from(ResourceTranslationItem.MODEL_MODEL)
                .eq(ResourceTranslationItem::getResLangCode, resLangCode)
                .eq(ResourceTranslationItem::getLangCode, data.getLangCode())
                .eq(ResourceTranslationItem::getScope, TranslationApplicationScopeEnum.GLOBAL)
                .gt(ResourceTranslationItem::getId, 0)
                .isNotNull(ResourceTranslationItem::getTarget);

        long count = new ResourceTranslationItem().count(queryWrapper);
        int total = (int) Math.ceil((double) count / 1000); // 计算总页数
        CountDownLatch countDown = new CountDownLatch(total);
        for (int i = 1; i <= total; i++) {
            int finalI = i;
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    Pagination<ResourceTranslationItem> page = new Pagination<>();
                    page.setCurrentPage(finalI);
                    page.setSize(1000L);
                    List<ResourceTranslationItem> resourceTranslationItems = Models.data().queryListByWrapper(page, queryWrapper.select(ResourceTranslationItem::getState, ResourceTranslationItem::getOrigin, ResourceTranslationItem::getTarget));
                    if (CollectionUtils.isNotEmpty(resourceTranslationItems)) {
                        for (ResourceTranslationItem resourceTranslationItem : resourceTranslationItems) {
                            if (Boolean.TRUE.equals(resourceTranslationItem.getState())) {
                                maps.put(resourceTranslationItem.getOrigin(), resourceTranslationItem.getTarget());
                            }
                        }
                    }
                } catch (Throwable e) {
                    log.error("查询全局所属应用的翻译值异常", e);
                } finally {
                    countDown.countDown();
                }
            });
        }

        try {
            countDown.await();
        } catch (InterruptedException e) {
            log.error("查询全局所属应用的翻译值异常", e);
        }
        String targetLanguageISOCode = Optional.ofNullable(queryByCode(data.getLangCode()))
                .map(ResourceLang::getIsoCode)
                .orElseThrow(() -> PamirsException.construct(UNKNOWN_LANG_ERROR).errThrow());
        ResourceTranslation updateTranslation = new ResourceTranslation();
        updateTranslation.setModule(data.getModule());
        updateTranslation.setResLangCode(data.getResLangCode());
        updateTranslation.setLangCode(data.getLangCode());
        updateTranslation.setRemoteUrl(upload(fileClient, generatorPath(TranslateConstants.TRANSLATE_GLOBAL_PATH), targetLanguageISOCode, JSON.toJSONString(maps)));
        updateTranslation.updateByUnique();
    }

    /**
     * 查询源术语所属应用的翻译值
     *
     * @param data
     * @param resLangCode
     * @param fileClient
     */
    private static void queryTranslationModule(ResourceTranslation data, String resLangCode, FileClient fileClient) {
        Map<String, String> maps = new ConcurrentHashMap<>();

        LambdaQueryWrapper<TranslationItemProxy> queryWrapper = Pops.<TranslationItemProxy>lambdaQuery()
                .from(TranslationItemProxy.MODEL_MODEL)
                .eq(TranslationItemProxy::getModule, data.getModule())
                .eq(TranslationItemProxy::getResLangCode, resLangCode)
                .eq(TranslationItemProxy::getLangCode, data.getLangCode())
                .eq(TranslationItemProxy::getScope, TranslationApplicationScopeEnum.MODULE)
                .gt(TranslationItemProxy::getId, 0)
                .isNotNull(TranslationItemProxy::getTarget);

        long count = new TranslationItemProxy().count(queryWrapper);
        int total = (int) Math.ceil((double) count / 1000); // 计算总页数
        CountDownLatch countDown = new CountDownLatch(total);
        for (int i = 1; i <= total; i++) {
            int finalI = i;
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    Pagination<TranslationItemProxy> page = new Pagination<>();
                    page.setCurrentPage(finalI);
                    page.setSize(1000L);
                    LambdaQueryWrapper<TranslationItemProxy> itemQw = queryWrapper.select(TranslationItemProxy::getState, TranslationItemProxy::getOrigin, TranslationItemProxy::getTarget);
                    List<TranslationItemProxy> translationItemProxies = Models.data().queryListByWrapper(page, itemQw);
                    if (!translationItemProxies.isEmpty()) {
                        for (TranslationItemProxy translationItemProxy : translationItemProxies) {
                            if (Boolean.TRUE.equals(translationItemProxy.getState())) {
                                maps.put(translationItemProxy.getOrigin(), translationItemProxy.getTarget());
                            }
                        }
                    }
                } catch (Throwable e) {
                    log.info("查询源术语所属应用的翻译值异常", e);
                } finally {
                    countDown.countDown();
                }
            });
        }
        try {
            countDown.await();
        } catch (InterruptedException e) {
            log.error("查询源术语所属应用的翻译值异常", e);
        }
        //清理oss上存在的文件
//        fileClient.deleteByFolder(generatorPath(data.getModuleName()));
        String targetLanguageISOCode = Optional.ofNullable(queryByCode(data.getLangCode()))
                .map(ResourceLang::getIsoCode)
                .orElseThrow(() -> PamirsException.construct(UNKNOWN_LANG_ERROR).errThrow());
        ResourceTranslation updateTranslation = new ResourceTranslation();
        updateTranslation.setModule(data.getModule());
        updateTranslation.setResLangCode(data.getResLangCode());
        updateTranslation.setLangCode(data.getLangCode());
        updateTranslation.setRemoteUrl(upload(fileClient, generatorPath(data.getModuleName()), targetLanguageISOCode, JSON.toJSONString(maps)));
        updateTranslation.updateByUnique();
    }

    private static ResourceLang queryByCode(String code) {
        ResourceLang query = new ResourceLang();
        query.setCode(code);
        return Models.data().queryOne(query);
    }

    /**
     * 删除这个目录下的所有文件
     * <p>
     * https://relx-kaiyang.oss-cn-zhangjiakou.aliyuncs.com/upload/libra00/pamirs/translate
     * fixme tim 删除远程的OSS文件
     *
     * @param fileClient
     */
    private void delete(FileClient fileClient) {
        String filePath = TranslateConstants.TRANSLATE_PATH;
        fileClient.deleteByFolder(filePath);
    }

    private static String generatorPath(String module) {
        return TranslateConstants.TRANSLATE_PATH + CharacterConstants.SEPARATOR_SLASH + module + CharacterConstants.SEPARATOR_SLASH;
    }

    private static String upload(FileClient fileClient, String path, String language, String data) {
        data = TranslateConstants.DEFAULT_TRANSLATION_JSONP_FUNCTION_NAME + CharacterConstants.LEFT_BRACKET + data + CharacterConstants.RIGHT_BRACKET;
        String filePath = path +
                TranslateConstants.DEFAULT_TRANSLATION_RESOURCE_FILE_NAME_PREFIX +
                language +
                CharacterConstants.SEPARATOR_UNDERLINE + System.currentTimeMillis() +
                TranslateConstants.DEFAULT_TRANSLATION_RESOURCE_FILE_NAME_SUFFIX;
        CdnFile file = fileClient.upload(filePath, data.getBytes(StandardCharsets.UTF_8));
        String downloadUrl = file.getUrl();
        log.info("ResourceTranslation jsonp path: {}", downloadUrl);
        return downloadUrl;
    }

    /**
     * 解析XML 模型
     *
     * @param model  模型
     * @param action action
     * @param module 应用
     * @return 当前页面的字段
     */
    private TranslationItemPageNodeVisitor getTranslationDslNodeVisitor(String model, String action, String module) {
        Set<String> map = new HashSet<>();
        TranslationItemPageNodeVisitor translationItemPageNodeVisitor = new TranslationItemPageNodeVisitor(map);
        parseHomePage(module, translationItemPageNodeVisitor);
        parseMenu(module, translationItemPageNodeVisitor);
        ViewAction viewAction = new ViewAction().setModel(model).setName(action);
        viewAction = viewAction.queryOne();
        if (viewAction != null) {
            String displayName = viewAction.getDisplayName();
            if (StringUtils.isNotBlank(displayName)) {
                translationItemPageNodeVisitor.context.add(displayName);
            }
            String title = viewAction.getTitle();
            if (StringUtils.isNotBlank(title)) {
                translationItemPageNodeVisitor.context.add(title);
            }
            try {
                View view = fetchMainView(viewAction);
                UIView uiView = DslParser.parser(view);
                TranslateCompileContext context = new TranslateCompileContext(viewAction.getModule());
                translationItemPageNodeVisitor.setCurrentContext(context);
                DslParser.visit(uiView, translationItemPageNodeVisitor);
            } catch (Exception e) {
                log.error("解析XML文件 模型为：{}", viewAction.getModel());
            }
        }

        if (TranslateConstants.APPS.equals(module)) {
            List<ModuleCategory> moduleCategorys = new ModuleCategory().queryList();
            List<ModuleDefinition> moduleDefinitionList = new ModuleDefinition().queryList();
            if (CollectionUtils.isNotEmpty(moduleCategorys)) {
                Set<String> moduleCategoryNames = moduleCategorys.stream().map(ModuleCategory::getName).collect(Collectors.toSet());
                translationItemPageNodeVisitor.context.addAll(moduleCategoryNames);
            }
            if (CollectionUtils.isNotEmpty(moduleDefinitionList)) {
                Set<String> moduleDisplayNames = moduleDefinitionList.stream().map(ModuleDefinition::getDisplayName).collect(Collectors.toSet());
                translationItemPageNodeVisitor.context.addAll(moduleDisplayNames);
            }
        }
        return translationItemPageNodeVisitor;
    }

    private static void parseHomePage(String module, TranslationItemPageNodeVisitor translationItemPageNodeVisitor) {
        UeModule ueModule = new UeModule().queryOneByWrapper(Pops.<UeModule>lambdaQuery()
                .from(UeModule.MODEL_MODEL)
                .eq(UeModule::getModule, module));
        if (ueModule != null) {
            ueModule.setHomePageModel(ueModule.getHomePageModel());
            ueModule.setHomePageName(ueModule.getHomePageName());
            ueModule.fieldQuery(UeModule::getHomePage);
            if (StringUtils.isNotBlank(ueModule.getDisplayName())) {
                translationItemPageNodeVisitor.context.add(ueModule.getDisplayName());
            }
            ViewAction homePage = ueModule.getHomePage();
            if (homePage != null && StringUtils.isNotBlank(homePage.getModule()) && PamirsSession.getContext().getModule(homePage.getModule()).getApplication()) {
                String displayName = homePage.getDisplayName();
                if (StringUtils.isNotBlank(displayName)) {
                    translationItemPageNodeVisitor.context.add(displayName);
                }
                String title = homePage.getTitle();
                if (StringUtils.isNotBlank(title)) {
                    translationItemPageNodeVisitor.context.add(title);
                }
            }
        }
    }

    private static void parseMenu(String module, TranslationItemPageNodeVisitor translationItemPageNodeVisitor) {
        List<Menu> menus = new Menu().queryList(Pops.<Menu>lambdaQuery().from(Menu.MODEL_MODEL).eq(Menu::getModule, module));
        if (CollectionUtils.isNotEmpty(menus)) {
            menus = new Menu().listFieldQuery(menus, Menu::getViewAction);
            for (Menu menu : menus) {
                String displayName = menu.getDisplayName();
                if (StringUtils.isNotBlank(displayName)) {
                    translationItemPageNodeVisitor.context.add(displayName);
                }
                ViewAction viewAction = menu.getViewAction();
                if (viewAction == null) {
                    continue;
                }
                String title = viewAction.getTitle();
                if (StringUtils.isNotBlank(title)) {
                    translationItemPageNodeVisitor.context.add(title);
                }
            }
        }
    }

    protected View fetchMainView(ViewAction viewAction) {
        String resModel = Optional.ofNullable(viewAction.getResModel()).orElse(viewAction.getModel());
        View resView = viewAction.getResView();
        View mainView;
        if (resView == null || resView.getTemplate() == null) {
            mainView = metaCacheManager.fetchView(resModel, viewAction.getResViewName(), viewAction.getViewType());
        } else {
            mainView = resView;
        }
        return mainView;
    }

    /**
     * 解析模型
     *
     * @param tContext
     * @param module
     */
    private void parseModel(Set<String> tContext, String module) {
        LambdaQueryWrapper<ModelDefinition> qw = Pops.<ModelDefinition>lambdaQuery().from(ModelDefinition.MODEL_MODEL);
        if (StringUtils.isNotBlank(module)) {
            if (MODULES_TO_EXCLUDE.contains(module)) {
                return;
            }
            qw.eq(StringUtils.isNotBlank(module), ModelDefinition::getModule, module);
        } else {
            qw.notIn(ModelDefinition::getModule, MODULES_TO_EXCLUDE);
        }
        qw.select(ModelDefinition::getDisplayName);
        List<ModelDefinition> modelDefinitions = new ModelDefinition().queryList(qw);
        if (CollectionUtils.isNotEmpty(modelDefinitions)) {
            for (ModelDefinition modelDefinition : modelDefinitions) {
                String displayName = modelDefinition.getDisplayName();
                if (StringUtils.isNotBlank(displayName)) {
                    tContext.add(displayName);
                }
            }
        }
    }

    /**
     * 解析枚举
     *
     * @param tContext
     * @param module
     */
    private void parseModelEnum(Set<String> tContext, String module) {
        LambdaQueryWrapper<DataDictionary> qw = Pops.<DataDictionary>lambdaQuery().from(DataDictionary.MODEL_MODEL);
        if (StringUtils.isNotBlank(module)) {
            if (MODULES_TO_EXCLUDE.contains(module)) {
                return;
            }
            qw.eq(StringUtils.isNotBlank(module), DataDictionary::getModule, module);
        } else {
            qw.notIn(DataDictionary::getModule, MODULES_TO_EXCLUDE);
        }
        qw.select(DataDictionary::getOptions);
        List<DataDictionary> dataDictionaries = new DataDictionary().queryList(qw);
        if (CollectionUtils.isNotEmpty(dataDictionaries)) {
            for (DataDictionary dataDictionary : dataDictionaries) {
                List<DataDictionaryItem> dictionaryItems = dataDictionary.getOptions();
                if (CollectionUtils.isNotEmpty(dictionaryItems)) {
                    for (DataDictionaryItem dictionaryItem : dictionaryItems) {
                        String displayName = dictionaryItem.getDisplayName();
                        if (StringUtils.isNotBlank(displayName)) {
                            tContext.add(displayName);
                        }
                    }
                }
            }
        }
    }

    /**
     * 生成系统中未翻译的字段 带筛选条件
     *
     * @param langCode     源语言
     * @param module       模块
     * @param resLangCode  目标语言
     * @param originSet    需要翻译的字段
     * @param originSearch 要模糊匹配的中文字符串
     * @return
     */
    private List<TranslationItemProxy> getTranslationItemExportProxies(String langCode, String module, String resLangCode, Set<String> originSet, String originSearch) {
        List<TranslationItemProxy> translationItemProxies = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(originSet)) {
            for (String origin : originSet) {
                if (FIELD_TO_EXCLUDE.contains(origin)) {
                    continue;
                }
                if (StringUtils.isBlank(originSearch)) {
                    TranslationItemProxy item = new TranslationItemProxy();
                    item.setModule(module);
                    item.setResLangCode(resLangCode);
                    item.setLangCode(langCode);
                    item.setOrigin(origin);
                    item.setState(true);
                    item.setScope(TranslationApplicationScopeEnum.MODULE);
                    item.setComments("");
                    translationItemProxies.add(item);
                } else {
                    if (origin.contains(originSearch)) {
                        TranslationItemProxy item = new TranslationItemProxy();
                        item.setModule(module);
                        item.setResLangCode(resLangCode);
                        item.setLangCode(langCode);
                        item.setOrigin(origin);
                        item.setState(true);
                        item.setScope(TranslationApplicationScopeEnum.MODULE);
                        item.setComments("");
                        translationItemProxies.add(item);
                    }
                }
            }
        }
        return translationItemProxies;
    }

    //异步获取数据库中以翻译的值（源术语所在应用）
    private static CompletableFuture<Map<String, TranslationItemProxy>> fetchAsyncTranslatedValueForApp(String langCode, String resLangCode, String module) {
        CompletableFuture<Map<String, TranslationItemProxy>> moduleFuture = CompletableFuture.supplyAsync(() -> {
            LambdaQueryWrapper<TranslationItemProxy> queryWrapper = new LambdaQueryWrapper<TranslationItemProxy>()
                    .from(TranslationItemProxy.MODEL_MODEL)
                    .eq(TranslationItemProxy::getLangCode, langCode)
                    .eq(TranslationItemProxy::getResLangCode, resLangCode)
                    .eq(TranslationItemProxy::getModule, module)
                    .eq(TranslationItemProxy::getScope, TranslationApplicationScopeEnum.MODULE)
                    .isNotNull(TranslationItemProxy::getTarget);

            List<TranslationItemProxy> dbItemList = new TranslationItemProxy().queryList(queryWrapper);
            return dbItemList.stream().collect(Collectors.toMap(TranslationItemProxy::getOrigin, java.util.function.Function.identity()));
        }, TtlAsyncTaskExecutor.getExecutorService());
        return moduleFuture;
    }

    // 异步获取数据库中以翻译的值（全局）
    private static CompletableFuture<Set<String>> fetchGlobalAsyncTranslatedValue(String langCode, String resLangCode, String module) {
        CompletableFuture<Set<String>> systemFuture = CompletableFuture.supplyAsync(() -> {
            LambdaQueryWrapper<TranslationItemProxy> queryWrapper = new LambdaQueryWrapper<TranslationItemProxy>()
                    .from(TranslationItemProxy.MODEL_MODEL)
                    .eq(TranslationItemProxy::getLangCode, langCode)
                    .eq(TranslationItemProxy::getResLangCode, resLangCode)
                    .eq(TranslationItemProxy::getModule, module)
                    .eq(TranslationItemProxy::getScope, TranslationApplicationScopeEnum.MODULE)
                    .eq(TranslationItemProxy::getSystem, Boolean.TRUE)
                    .isNull(TranslationItemProxy::getTarget);

            List<TranslationItemProxy> dbItemList = new TranslationItemProxy().queryList(queryWrapper);
            return dbItemList.stream().map(TranslationItemProxy::getOrigin).collect(Collectors.toSet());
        }, TtlAsyncTaskExecutor.getExecutorService());
        return systemFuture;
    }
}
