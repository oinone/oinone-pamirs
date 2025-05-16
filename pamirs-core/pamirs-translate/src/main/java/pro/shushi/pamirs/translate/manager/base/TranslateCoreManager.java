package pro.shushi.pamirs.translate.manager.base;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.pojo.TranslatePojo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService.uniqueTranslateFunction;

/**
 * 翻译管理器
 *
 * @author xzf 2022/12/20 11:30
 **/
@Component
@Slf4j
public class TranslateCoreManager {

    @Autowired
    private TranslateRedisManager translateRedisManager;

    /**
     * 扫描元数据，将所有元数据中的中文存到翻译项中
     */
    public List<ResourceTranslationItem> exportAll() {

        Map<String, ResourceTranslationItem> metaItemMap = new ConcurrentHashMap<>();

        // 元数据
        long start = System.currentTimeMillis();
        log.info("翻译元数据计算开始");
        @SuppressWarnings("rawtypes")
        List<TranslateMetaBaseService> serviceList = BeanDefinitionUtils.getBeansOfTypeByOrdered(TranslateMetaBaseService.class);
        CountDownLatch countDown = new CountDownLatch(serviceList.size());
        for (TranslateMetaBaseService<?> entry : serviceList) {
            final String initType = entry.initModelType();
            if (StringUtils.isBlank(initType)) {
                countDown.countDown();
                continue;
            }
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    List<ResourceTranslationItem> items = entry.fetchMetaDataPojo();
                    for (ResourceTranslationItem item : items) {
                        String key = TranslatePojo.originUnique(item);
                        metaItemMap.put(key, item);
                    }
                } catch (Throwable e) {
                    log.error("获取翻译异常", e);
                } finally {
                    countDown.countDown();
                }
            });
        }
        try {
            boolean await = countDown.await(10, TimeUnit.MINUTES);
            log.info("获取翻译等待: {}", await);
        } catch (InterruptedException e) {
            log.error("获取翻译等待异常", e);
        }

        List<ResourceTranslationItem> result = hintTranslated(metaItemMap);
        log.info("翻译元数据计算,总共元数据:{} 总共耗时:{} ms", result.size(), (System.currentTimeMillis() - start));
        return result;
    }

    public List<ResourceTranslationItem> hintTranslated(Map<String, ResourceTranslationItem> metaItemMap) {
        IWrapper<ResourceTranslationItem> qw = Pops.<ResourceTranslationItem>lambdaQuery().from(ResourceTranslationItem.MODEL_MODEL);
        HashSet<String> keys = new HashSet<>();
        ResourceTranslationItem query = new ResourceTranslationItem();
        long total = query.count(qw);
        int page = Long.valueOf(total / 2000).intValue() + 1;

        Map<String, String> moduleDisplayNameMap = new TranslateModuleManager(Boolean.FALSE).getModuleDisplayNameMap();
        List<ResourceTranslationItem> result = new CopyOnWriteArrayList<>();
        CountDownLatch countDown = new CountDownLatch(page);
        for (int i = 1; i <= page; i++) {
            final int fi = i;
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    Pagination<ResourceTranslationItem> pagination = new Pagination<>();
                    pagination.setCurrentPage(fi);
                    pagination.setSize(2000L);
                    List<ResourceTranslationItem> dbItemList = new ResourceTranslationItem().queryListByWrapper(pagination, qw);
                    if (CollectionUtils.isEmpty(dbItemList)) {
                        return;
                    }
                    log.info("第{}页: {}", fi, dbItemList.size());
                    for (ResourceTranslationItem item : dbItemList) {
                        if (null == item) {
                            continue;
                        }
                        if (StringUtils.isAnyBlank(
                                        item.getModule(),
                                        item.getResLangCode(),
                                        item.getLangCode(),
                                        item.getOrigin(),
                                        item.getModel(),
                                        item.getTarget())) {
                            continue;
                        }

                        if (null == item.getState() || !item.getState()) {
                            continue;
                        }

                        String key = TranslatePojo.originUnique(item);

                        ResourceTranslationItem metaItem = metaItemMap.get(key);

                        keys.add(key);

                        // 手动录入翻译数据
                        if (null == metaItem) {
                            String moduleDisplayName = moduleDisplayNameMap.get(item.getModule());
                            item.setModule(moduleDisplayName);
                            result.add(item);
                        } else {
                            // 元数据数据
                            String moduleDisplayName = moduleDisplayNameMap.get(metaItem.getModule());
                            metaItem.setModule(moduleDisplayName);
                            metaItem.setState(false);
                            result.add(metaItem);
                        }
                    }
                } catch (Throwable e) {
                    log.info("计算翻译异常", e);
                } finally {
                    countDown.countDown();
                }
            });
        }

        try {
            boolean await = countDown.await(10, TimeUnit.MINUTES);
            log.error("计算翻译等待: {}", await);
        } catch (InterruptedException e) {
            log.error("计算翻译等待异常", e);
        }

        if (result.isEmpty() && MapUtils.isNotEmpty(metaItemMap)) {
            return new ArrayList<>(metaItemMap.values());
        }

        if (CollectionUtils.isNotEmpty(keys)){
            for (String key : keys) {
                metaItemMap.remove(key);
            }
        }

        for (String key : metaItemMap.keySet()) {
            ResourceTranslationItem metaItem = metaItemMap.get(key);
            String moduleDisplayName = moduleDisplayNameMap.get(metaItem.getModule());
            metaItem.setModule(moduleDisplayName);
            metaItem.setState(false);
            result.add(metaItem);
        }

        return result;
    }

    public void initTranslateFromDbToRedis() {
        Pagination<ResourceTranslation> allTranslate = new Pagination<ResourceTranslation>().setSize(Long.MAX_VALUE);
        List<ResourceTranslation> dbTranslateList = new ResourceTranslation().queryListByWrapper(allTranslate, Pops.<ResourceTranslation>lambdaQuery().from(ResourceTranslation.MODEL_MODEL));
        Map<String/*unique*/, ResourceTranslation> translationDbMap = Optional.ofNullable(dbTranslateList)
                .map(List::stream)
                .orElse(Stream.empty())
                .collect(Collectors.toMap(uniqueTranslateFunction, Function.identity(), (_a, _b) -> _a));

        IWrapper<ResourceTranslationItem> qw = Pops.<ResourceTranslationItem>lambdaQuery().from(ResourceTranslationItem.MODEL_MODEL);
        ResourceTranslationItem query = new ResourceTranslationItem();
        long total = query.count(qw);

        int page = Long.valueOf(total / 2000).intValue() + 1;
        AtomicInteger totalA = new AtomicInteger();
        /* itemKey itemHashKey value */
        Map<String, Map<String, String>> itemDbMap = new ConcurrentHashMap<>();
        CountDownLatch countDown = new CountDownLatch(page);
        for (int i = 1; i <= page; i++) {
            final int fi = i;
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    Pagination<ResourceTranslationItem> pagination = new Pagination<>();
                    pagination.setCurrentPage(fi);
                    pagination.setSize(2000L);
                    List<ResourceTranslationItem> dbItemList = new ResourceTranslationItem().queryListByWrapper(pagination, qw);
                    if (CollectionUtils.isEmpty(dbItemList)) {
                        return;
                    }
                    log.info("第{}页: {}", fi, dbItemList.size());
                    for (ResourceTranslationItem item : dbItemList) {
                        if (null == item) {
                            continue;
                        }
                        totalA.incrementAndGet();

                        if (
                                StringUtils.isAnyBlank(
                                        item.getModule(),
                                        item.getResLangCode(),
                                        item.getLangCode(),
                                        item.getOrigin(),
                                        item.getModel(),
                                        item.getTarget()
                                )
                        ) {
                            continue;
                        }

                        if (null == item.getState() || !item.getState()) {
                            continue;
                        }

                        TranslatePojo pojo = TranslatePojo.of(item);

                        String itemKey = pojo.itemKey();
                        String itemHashKey = pojo.itemHashKey();
                        itemDbMap.computeIfAbsent(itemKey, _itemKey -> new HashMap<>())
                                .computeIfAbsent(itemHashKey, _itemHashKey -> JSON.toJSONString(pojo));
                    }

                } catch (Throwable e) {
                    log.error("缓存元数据异常", e);
                } finally {
                    countDown.countDown();
                }
            });
        }

        try {
            boolean await = countDown.await(10, TimeUnit.MINUTES);
            log.info("缓存翻译等待: {}", await);
        } catch (InterruptedException e) {
            log.error("缓存翻译等待异常", e);
        }

        translateRedisManager.delAllTranslation();
        translateRedisManager.delAllItem();
        log.info("删除翻译元数据Redis缓存");
        translateRedisManager.putAllItem(itemDbMap);
        translateRedisManager.putAllTranslation(translationDbMap);
        log.info("初始化翻译元数据完成：key[{}] 翻译项{}条，翻译:{}条", itemDbMap.size(), totalA, translationDbMap.size());
    }

}
