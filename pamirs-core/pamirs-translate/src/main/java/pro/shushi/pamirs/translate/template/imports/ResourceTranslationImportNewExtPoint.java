package pro.shushi.pamirs.translate.template.imports;

import com.alibaba.excel.exception.ExcelAnalysisException;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.enmu.ExcelTaskStateEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.resource.api.enmu.TranslateDataSourcesEnum;
import pro.shushi.pamirs.resource.api.enmu.TranslationApplicationScopeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.constant.TranslateConstants;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.pojo.TranslatePojo;
import pro.shushi.pamirs.translate.task.ResourceTranslationImportTask;
import pro.shushi.pamirs.translate.template.TranslateTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xzf 2023/1/12 17:06
 */
@Slf4j
@Component
@Ext(ExcelImportTask.class)
public class ResourceTranslationImportNewExtPoint extends AbstractExcelImportDataExtPointImpl<ResourceTranslationItem> {

    @Autowired
    private ResourceTranslationImportTask resourceTranslationImportTask;

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + TranslateTemplate.TEMPLATE_NAME + "\" &&  importContext.definitionContext.model==\"" + ResourceTranslation.MODEL_MODEL + "\"")
    public Boolean importData(ExcelImportContext importContext, ResourceTranslationItem data) {
        List<Object> dataBufferList = importContext.getDataBufferList();
        ExcelImportTask importTask = importContext.getImportTask();

        if (CollectionUtils.isEmpty(dataBufferList)) {
            //初始化校验文件
            initTranslation(dataBufferList);
        }
        //获取校验文件
        Map<String, String> globalTranslationMap = (Map<String, String>) dataBufferList.get(0);
        List<String> resourceLangList = (List<String>) dataBufferList.get(1);
        Map<String, String> displayNameModuleMap = (Map<String, String>) dataBufferList.get(2);
        Map<String, String> globalItemMetaMap = (Map<String, String>) dataBufferList.get(3);

        //校验翻译资源项
        verifyTranslationItem(data, globalTranslationMap, resourceLangList, displayNameModuleMap, globalItemMetaMap);

        List<ResourceTranslationItem> list = (List<ResourceTranslationItem>) dataBufferList.get(4);
        list.add(data);

        if (!importContext.getCurrentListener().hasNext()) {
            try {
                calcExcel(list, importTask);
                log.info("翻译项导入完成");
                importTask.setState(ExcelTaskStateEnum.PROCESSING);
                return true;
            } catch (PamirsException e) {
                log.error("翻译项导入失败", e);
                importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, e.getMessage());
                return true;
            } catch (Exception e) {
                log.error("翻译项导入失败", e);
                importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "翻译项导入失败");
                return true;
            }
        }
        return true;
    }

    private static void initTranslation(List<Object> dataBufferList) {
        Map<String, String> globalTranslationMap = new ConcurrentHashMap<>();
        LambdaQueryWrapper<ResourceTranslationItem> queryWrapper = Pops.<ResourceTranslationItem>lambdaQuery()
                .from(ResourceTranslationItem.MODEL_MODEL)
                .eq(ResourceTranslationItem::getScope, TranslationApplicationScopeEnum.GLOBAL)
                .isNotNull(ResourceTranslationItem::getTarget)
                .gt(ResourceTranslationItem::getId, 0);

        long count = new ResourceTranslationItem().count(queryWrapper);
        // 计算总页数
        int total = (int) Math.ceil((double) count / 1000);
        CountDownLatch countDown = new CountDownLatch(total);
        for (int i = 1; i <= total; i++) {
            int finalI = i;
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    Pagination<ResourceTranslationItem> page = new Pagination<>();
                    page.setCurrentPage(finalI);
                    page.setSize(1000L);
                    List<ResourceTranslationItem> resourceTranslationItems = Models.data().queryListByWrapper(page, queryWrapper);
                    if (CollectionUtils.isNotEmpty(resourceTranslationItems)) {
                        Map<String, String> collect = resourceTranslationItems.stream()
                                .filter(item -> Boolean.TRUE.equals(item.getState()))
                                .collect(Collectors.toMap(item -> String.join(",", item.getOriginCode(), item.getResLangCode(), item.getLangCode()),
                                        ResourceTranslationItem::getModule));

                        globalTranslationMap.putAll(collect);
                    }
                } catch (Exception e) {
                    log.info("导入翻译异常", e);
                } finally {
                    countDown.countDown();
                }
            });
        }

        List<ResourceLang> resourceLangs = new ResourceLang().queryList();
        List<String> resourceLangList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resourceLangs)) {
            resourceLangList = resourceLangs.stream().map(ResourceLang::getCode).collect(Collectors.toList());
        } else {
            resourceLangList.add(TranslateConstants.LANG_CODE);
        }

        Map<String, String> displayNameModuleMap = getDisplayNameModuleMap();
        try {
            countDown.await();
        } catch (InterruptedException e) {
            log.error("导入翻译等待异常", e);
        }
        dataBufferList.add(0, globalTranslationMap);
        dataBufferList.add(1, resourceLangList);
        dataBufferList.add(2, displayNameModuleMap);
        dataBufferList.add(3, new HashMap<>());
        dataBufferList.add(4, new ArrayList<>());
    }

    private void calcExcel(List<ResourceTranslationItem> excelItemList, ExcelImportTask importTask) {
        if (CollectionUtils.isEmpty(excelItemList)) {
            return;
        }
        log.info("翻译项导入Excel数据共有：{}", excelItemList.size());
        Map<String, ResourceTranslationItem> itemMetaMap = new HashMap<>();
        Map<String, ResourceTranslation> translationMap = new HashMap<>();

        boolean hasError = Boolean.FALSE;
        for (ResourceTranslationItem excelItem : excelItemList) {
            TranslatePojo dbItem = TranslatePojo.of(excelItem);
            String itemUnique = dbItem.uniqueKey();
            //翻译资源项存储
            itemMetaMap.putIfAbsent(itemUnique, excelItem);

            String translationUniqueKey = dbItem.uniqueKeyTranslation();
            ResourceTranslation resourceTranslation = dbItem.toTranslation();
            //翻译资源存储
            translationMap.putIfAbsent(translationUniqueKey, resourceTranslation);
        }

        log.info("翻译项导入Excel计算后共有数据:{}", itemMetaMap.size());
        List<List<ResourceTranslationItem>> itemMetaSplitList = Lists.partition(Lists.newArrayList(itemMetaMap.values()), 500);
        int threadMetaNum = itemMetaSplitList.size();
        CountDownLatch threadSignal = new CountDownLatch(threadMetaNum);
        for (List<ResourceTranslationItem> subMap : itemMetaSplitList) {
            log.info("翻译项元数据导入切分单个任务处理数据:{}", subMap.size());
            TranslateItemSaveRunnable translateItemSaveRunnable = new TranslateItemSaveRunnable(subMap, threadSignal);
            TtlAsyncTaskExecutor.getExecutorService().execute(translateItemSaveRunnable);
        }

        new ResourceTranslation().createOrUpdateBatch(new ArrayList<>(translationMap.values()));
        try {
            threadSignal.await(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            try (StringWriter expWriter = new StringWriter(); PrintWriter expPWriter = new PrintWriter(expWriter)) {
                e.printStackTrace(expPWriter);
                String error = expWriter.toString();
                importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "导入翻译数据异常:" + error);
                log.error("导入翻译数据异常:" + error);
            } catch (IOException exp) {
                log.error("导入翻译数据异常", exp);
            }
            updateImportTask(hasError, importTask);
        }
    }

    public void updateImportTask(Boolean hasError, ExcelImportTask importTask) {
        if (null == importTask.getMessages()) {
            importTask.setMessages(new ArrayList<>());
        }
        if (!hasError) {
            importTask.setState(ExcelTaskStateEnum.SUCCESS).addTaskMessage(TaskMessageLevelEnum.INFO, "导入成功");
        } else {
            importTask.setState(ExcelTaskStateEnum.FAILURE).addTaskMessage(TaskMessageLevelEnum.ERROR, "导入失败");
        }
        importTask.updateById();
    }

    private void verifyTranslationItem(ResourceTranslationItem excelItem,
                                       Map<String, String> globalTranslationMap,
                                       List<String> resourceLangList,
                                       Map<String, String> displayNameModuleMap,
                                       Map<String, String> globalItemMetaMap) {
        if (null == excelItem) {
            return;
        }
        StringBuilder errorMessage = new StringBuilder();
        excelItem.setResLangCode(TranslateConstants.RES_LANG_CODE);
        if (StringUtils.isBlank(excelItem.getModule())) {
            errorMessage.append(TranslateEnumerate.TRANSLATION_APPLICATION_FIELDS_CANNOT_BE_EMPTY.msg()).append(" ");
        }
        String module = displayNameModuleMap.get(excelItem.getModule());
        if (StringUtils.isBlank(module)) {
            errorMessage.append(TranslateEnumerate.TRANSLATION_APPLICATION_FIELD_DOES_NOT_EXIST.msg()).append(" ");
        }
        excelItem.setModule(module);
        if (StringUtils.isBlank(excelItem.getLangCode())) {
            errorMessage.append(TranslateEnumerate.TARGET_LANGUAGE_ENCODING_CANNOT_BE_EMPTY.msg()).append(" ");
        }
        if (!resourceLangList.contains(excelItem.getLangCode())) {
            errorMessage.append(PStringUtils.parse1(TranslateEnumerate.TARGET_LANGUAGE_ENCODING_ERROR.msg(), Arrays.asList(resourceLangList))).append(" ");
        }
        if (StringUtils.isBlank(excelItem.getOrigin())) {
            errorMessage.append(TranslateEnumerate.SOURCE_LANGUAGE_CANNOT_BE_EMPTY.msg()).append(" ");
        }
        if (StringUtils.isBlank(excelItem.getTarget())) {
            errorMessage.append(TranslateEnumerate.TARGET_LANGUAGE_CANNOT_BE_EMPTY.msg()).append(" ");
        }
        if (excelItem.getState() == null) {
            errorMessage.append(TranslateEnumerate.ACTIVATION_STATUS_CANNOT_BE_EMPTY.msg()).append(" ");
        }

        excelItem.initOriginCode();
        String globalTranslationKey = String.join(",", excelItem.getOriginCode(), excelItem.getResLangCode(), excelItem.getLangCode());

        if (excelItem.getScope() == null || !(excelItem.getScope().equals(TranslationApplicationScopeEnum.GLOBAL) || excelItem.getScope().equals(TranslationApplicationScopeEnum.MODULE))) {
            errorMessage.append(TranslateEnumerate.INVALID_TRANSLATION_SCOPE.msg()).append(" ");
        } else {
            if (excelItem.getScope().equals(TranslationApplicationScopeEnum.GLOBAL) && !globalTranslationMap.isEmpty() && globalTranslationMap.containsKey(globalTranslationKey)) {
                if (globalItemMetaMap.containsKey(globalTranslationKey) && (!globalItemMetaMap.get(globalTranslationKey).equals(excelItem.getModule()))) {
                    errorMessage.append(PStringUtils.parse1(TranslateEnumerate.THE_SOURCE_TERM_ALREADY_EXISTS_IN_CURRENT_EXCEL.msg(), excelItem.getOrigin())).append(" ");
                } else if (!((globalTranslationMap.get(globalTranslationKey)).equals(excelItem.getModule()))) {
                    errorMessage.append(PStringUtils.parse1(TranslateEnumerate.SOURCE_TERM_ALREADY_EXISTS_IN_ANOTHER_MODULE.msg(), PamirsSession.getContext().getModule(globalTranslationMap.get(globalTranslationKey)).getDisplayName(), excelItem.getOrigin())).append(" ");
                }
            }
        }
        if (StringUtils.isNotEmpty(errorMessage)) {
            throw new ExcelAnalysisException(errorMessage.toString());
        }
        excelItem.setDataSource(TranslateDataSourcesEnum.FILE_IMPORT_TRANSLATION);

        //检查当前导入的Excel 中会不会出现相同全局应用范围
        globalItemMetaMap.put(globalTranslationKey, excelItem.getModule());
    }


    public static Map<String, String> getDisplayNameModuleMap() {
        Map<String, String> displayNameModuleMap = new HashMap<>();

        IWrapper<ModuleDefinition> qw = Pops.<ModuleDefinition>lambdaQuery()
                .from(ModuleDefinition.MODEL_MODEL)
                .setBatchSize(-1);

        List<ModuleDefinition> moduleList = new ModuleDefinition().queryList(qw);
        for (ModuleDefinition moduleDefinition : Optional.ofNullable(moduleList).orElse(Collections.emptyList())) {
            String displayName = moduleDefinition.getDisplayName();
            if (null == displayName) {
                continue;
            }
            displayNameModuleMap.put(displayName, moduleDefinition.getModule());
        }
        return displayNameModuleMap;
    }

    class TranslateItemSaveRunnable implements Runnable {

        private final List<ResourceTranslationItem> itemList;
        private final CountDownLatch threadsSignal;

        public TranslateItemSaveRunnable(List<ResourceTranslationItem> itemList, CountDownLatch threadSignal) {
            this.itemList = itemList;
            this.threadsSignal = threadSignal;
        }

        @Override
        public void run() {
            log.info("开启子线程执行翻译项导入操作,线程名:{}", Thread.currentThread().getName());
            try {
                new ResourceTranslationItem().createOrUpdateBatch(itemList);
            } catch (Exception e) {
                log.error("SaveItem Error", e);
            } finally {
                threadsSignal.countDown();
            }
            log.info("结束子线程执行翻译项导入操作,线程名:{}", Thread.currentThread().getName());
        }
    }
}
