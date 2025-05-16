package pro.shushi.pamirs.file.api.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.TranslateUtils;
import pro.shushi.pamirs.file.api.config.ExcelConstant;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.FileExpEnumerate;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang
 * @date 2020-11-10 12:14
 */
@Base
@Component
@Model.model(ExcelExportTask.MODEL_MODEL)
public class ExcelExportTaskAction extends AbstractExcelExportTaskAction<ExcelExportTask> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * workbookName改成存储字段，存在在导出过去中动态生成的ExcelWorkbookDefinition不落库的情况.
     * 兼容早期 workbookName 不存储问题.
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<ExcelExportTask> queryPage(Pagination<ExcelExportTask> page, QueryWrapper<ExcelExportTask> queryWrapper) {
        Pagination<ExcelExportTask> pagination = new ExcelExportTask().queryPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pagination.getContent())) {
            return pagination;
        }

        // 兼容早期 workbookName 不存储问题
        Map<Long, ExcelWorkbookDefinition> excelWorkbookDefinitionMap = new HashMap<>();
        List<ExcelExportTask> noWorkNameTasks = pagination.getContent().stream().filter(task -> StringUtils.isBlank(task.getWorkbookName())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(noWorkNameTasks)) {
            List<Long> workbookDefinitionIds = ListUtils.transform(noWorkNameTasks, ExcelExportTask::getWorkbookDefinitionId);
            if (CollectionUtils.isNotEmpty(workbookDefinitionIds)) {
                excelWorkbookDefinitionMap = FetchUtil.fetchMapByIds(ExcelWorkbookDefinition.class, workbookDefinitionIds);
            }
        }
        for (ExcelExportTask exportTask : pagination.getContent()) {
            String name = exportTask.getName();
            if (StringUtils.isNotBlank(name)) {
                if (name.startsWith(ExcelConstant.EXPORT_TASK_NAME)) {
                    name = name.substring(ExcelConstant.EXPORT_TASK_NAME.length());
                    exportTask.setName(TranslateUtils.translateValues(ExcelConstant.EXPORT_TASK_NAME) + name);
                }
            }

            ExcelWorkbookDefinition workbookDefinition = excelWorkbookDefinitionMap.get(exportTask.getWorkbookDefinitionId());
            if (workbookDefinition != null) {
                exportTask.setWorkbookName(workbookDefinition.getName());
            }
        }

        return pagination;
    }

    @Function.Advanced(displayName = "导出任务预处理", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public ExcelExportTask prepareCreateExportTask(ExcelExportTask data) {
        String requestId = UUIDUtil.getUUIDNumberString();
        stringRedisTemplate.opsForValue().set(requestId, JsonUtils.toJSONString(data), 1, TimeUnit.MINUTES);
        ExcelExportTask prepareResult = new ExcelExportTask();
        prepareResult.setRequestId(requestId);
        return prepareResult;
    }

    @Action(displayName = "导出", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.TABLE})
    @Override
    public ExcelExportTask createExportTask(ExcelExportTask data) {
        data = fetchPrepareExportTask(data);
        return super.createExportTask(data);
    }

    protected ExcelExportTask fetchPrepareExportTask(ExcelExportTask data) {
        String requestId = data.getRequestId();
        if (StringUtils.isNotBlank(requestId)) {
            String prepareString = stringRedisTemplate.opsForValue().get(requestId);
            if (StringUtils.isBlank(prepareString)) {
                throw PamirsException.construct(FileExpEnumerate.EXPORT_REQUEST_NOT_EXIST).errThrow();
            }
            data = JsonUtils.parseObject(prepareString, ExcelExportTask.class);
        }
        return data;
    }

    @Override
    protected void doExport(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        if (null != exportTask.getSync() && exportTask.getSync()) {
            excelFileService.doExportSync(exportTask, context);
        } else {
            excelFileService.doExportAsync(exportTask, context);
        }
    }
}
