package pro.shushi.pamirs.translate.task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelTaskStateEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.manager.base.TranslateModuleManager;
import pro.shushi.pamirs.translate.manager.base.TranslateRedisManager;
import pro.shushi.pamirs.translate.pojo.TranslatePojo;
import pro.shushi.pamirs.trigger.enmu.TriggerTimeAnchorEnum;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;
import pro.shushi.pamirs.trigger.service.ScheduleTaskActionService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xzf (xzf@shushi.pro)
 * @date 2022/5/7 1:07 下午
 */
@Slf4j
@Component
@Fun(ResourceTranslationImportTask.FUN_NAMESPACE)
public class ResourceTranslationImportTask {

    public static final String FUN_NAMESPACE = "translate.ResourceTranslationImportTask";
    public static final String DEPLOYMENT_STATUS_CHECK = "deploymentStatusCheck";
    public static final String TECH_NAME = "ResourceTranslationImport_Task";

    @Autowired(required = false)
    private ScheduleTaskActionService scheduleTaskActionService;

    @Autowired
    private TranslateRedisManager translateRedisManager;


    public void createStatusCheckTask(Long importTaskId) {
        ScheduleTaskAction taskAction = new ScheduleTaskAction();
        taskAction.setTechnicalName(TECH_NAME + ":" + importTaskId);
        taskAction.setLimitExecuteNumber(1);
        taskAction.setPeriodTimeValue(30);
        taskAction.setPeriodTimeUnit(TimeUnitEnum.SECOND);
        taskAction.setPeriodTimeAnchor(TriggerTimeAnchorEnum.START);
        taskAction.setLimitRetryNumber(0);
        taskAction.setDisplayName("翻译导入任务开启：任务id:" + importTaskId);
        taskAction.setDescription("翻译导入任务开启：任务id:" + importTaskId);
        taskAction.setExecuteNamespace(ResourceTranslationImportTask.FUN_NAMESPACE);
        taskAction.setExecuteFun(DEPLOYMENT_STATUS_CHECK);
        taskAction.setTaskType(TaskType.REMOTE_SCHEDULE_TASK.getValue());
        taskAction.setBizId(importTaskId);
        taskAction.setContext(String.valueOf(importTaskId));
        taskAction.setExecuteFunction(new FunctionDefinition().setTimeout(10 * 60000));
        taskAction.setActive(Boolean.TRUE);
        taskAction.setFirstExecuteTime(LocalDateTime.now().plusSeconds(5).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        scheduleTaskActionService.submit(taskAction);
    }

    @Function
    @Function.fun(DEPLOYMENT_STATUS_CHECK)
    @Function.Advanced(timeout = 60000)
    public Result<Void> deploymentStatusCheck(ScheduleItem item) {
        Result<Void> result = new Result<>();
        Long importTaskId = item.getBizId();
        ExcelImportTask importTask = new ExcelImportTask().queryById(importTaskId);
        updateImportTaskProcess(importTask);
        List<ResourceTranslationItem> param = translateRedisManager.getExcelSnapshot(importTaskId);
        calcExcel(param, importTask);
        translateRedisManager.delExcelSnapshot(importTaskId);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public void updateImportTask(Boolean hasError, ExcelImportTask importTask) {
        if (null == importTask.getMessages()) {
            importTask.setMessages(new ArrayList<>());
        }
        if (!hasError) {
            importTask.setState(ExcelTaskStateEnum.SUCCESS)
                    .addTaskMessage(TaskMessageLevelEnum.INFO, "导入成功");
        } else {
            importTask.setState(ExcelTaskStateEnum.FAILURE)
                    .addTaskMessage(TaskMessageLevelEnum.ERROR, "导入失败");
        }
        importTask.updateById();
    }

    public void updateImportTaskProcess(ExcelImportTask importTask) {
        importTask.setMessages(new ArrayList<>());
        importTask.setState(ExcelTaskStateEnum.PROCESSING);
        importTask.updateById();
    }

    private void calcExcel(List<ResourceTranslationItem> excelItemList, ExcelImportTask importTask) {

        if (CollectionUtils.isEmpty(excelItemList)) {
            return;
        }

        log.info("翻译项导入Excel数据共有：{}", excelItemList.size());

        Map<String, ResourceTranslationItem> itemMetaMap = new HashMap<>();
        Map<String, ResourceTranslation> translationMap = new HashMap<>();

        Map<String, String> displayNameModuleMap = new TranslateModuleManager(Boolean.FALSE).getDisplayNameModuleMap();

        boolean hasError = Boolean.FALSE;
        for (int i = 0; i < excelItemList.size(); i++) {
            ResourceTranslationItem excelItem = excelItemList.get(i);

            if (null == excelItem) {
                continue;
            }

            try {
                String module = displayNameModuleMap.get(excelItem.getModule());
                if (StringUtils.isBlank(module)) {
                    hasError = true;
                    // 行号：" + (i + 2).  表头占了一样，然后i从0开始循环。因此需要 + 2
                    importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "模块错误，行号：" + (i + 2));
                    continue;
                }
                excelItem.setModule(module);

                if (StringUtils.isBlank(excelItem.getModel())) {
                    hasError = true;
                    importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "模型/字典为空，行号：" + (i + 2));
                    continue;
                }

                if (StringUtils.isBlank(excelItem.getLangCode())) {
                    hasError = true;
                    importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "语言编码错误，行号：" + (i + 2));
                    continue;
                }

                if (StringUtils.isBlank(excelItem.getResLangCode())) {
                    hasError = true;
                    importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "源语言编码错误，行号：" + (i + 2));
                    continue;
                }

                if (StringUtils.isBlank(excelItem.getOrigin())) {
                    hasError = true;
                    importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "源术语，行号：" + (i + 2));
                    continue;
                }

                if (StringUtils.isBlank(excelItem.getTarget())) {
                    hasError = true;
                    importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "翻译值为空：" + (i + 2));
                    continue;
                }

                TranslatePojo dbItem = TranslatePojo.of(excelItem);
                excelItem.initOriginCode();
                String itemUnique = dbItem.uniqueKey();

                itemMetaMap.putIfAbsent(itemUnique, excelItem);

                String translationUniqueKey = dbItem.uniqueKeyTranslation();
                ResourceTranslation resourceTranslation = dbItem.toTranslation();
                translationMap.putIfAbsent(translationUniqueKey, resourceTranslation);

            } catch (Exception e) {
                log.error("翻译项导入异常信息", e);
                hasError = true;
                importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "导入数据异常，数据已经被过滤掉，数据所在行：" + i + "，查看数据:" + excelItem);
            }
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
        translateRedisManager.putAllTranslation(translationMap);
        try {
            threadSignal.await(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            try (StringWriter expWriter = new StringWriter();
                 PrintWriter expPWriter = new PrintWriter(expWriter)) {
                e.printStackTrace(expPWriter);
                String error = expWriter.toString();
                importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "导入翻译数据异常:" + error);
                log.error("导入翻译数据异常:" + error);
            } catch (IOException exp) {
                log.error("导入翻译数据异常", exp);
            }
        } finally {
            translateRedisManager.delLockImportKey();
        }

        updateImportTask(hasError, importTask);
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
                Map<String, Map<String, String>> cachaMap = new HashMap<>();
                for (ResourceTranslationItem item : itemList) {
                    TranslatePojo pojo = TranslatePojo.of(item);
                    String itemKey = pojo.itemKey();
                    if (!cachaMap.containsKey(itemKey)) {
                        cachaMap.put(itemKey, new HashMap<>());
                    } else {
                        cachaMap.get(itemKey).put(pojo.itemHashKey(), JSON.toJSONString(pojo));
                    }
                }
                if (!cachaMap.isEmpty()) {
                    translateRedisManager.putAllItem(cachaMap);
                }
            } catch (Exception e) {
                log.error("SaveItem Error", e);
            } finally {
                threadsSignal.countDown();
            }
            log.info("结束子线程执行翻译项导入操作,线程名:{}", Thread.currentThread().getName());
        }
    }
}
