package pro.shushi.pamirs.file.api.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.core.common.TranslateUtils;
import pro.shushi.pamirs.file.api.config.ExcelConstant;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.easyexcel.ExcelAnalysisEventListener;
import pro.shushi.pamirs.file.api.extpoint.ExcelImportM2mDataExtPoint;
import pro.shushi.pamirs.file.api.extpoint.ExcelImportO2mDataExtPoint;
import pro.shushi.pamirs.file.api.function.impl.BatchImportExcelReadCallback;
import pro.shushi.pamirs.file.api.function.impl.DefaultExcelReadCallback;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang
 * @date 2020-11-10 12:12
 */
@Base
@Component
@Model.model(ExcelImportTask.MODEL_MODEL)
public class ExcelImportTaskAction extends AbstractExcelImportTaskAction<ExcelImportTask> {

    public ExcelImportTaskAction(FileProperties fileProperties, ExcelFileService excelFileService) {
        super(fileProperties, excelFileService);
    }

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
    public Pagination<ExcelImportTask> queryPage(Pagination<ExcelImportTask> page, QueryWrapper<ExcelImportTask> queryWrapper) {
        Pagination<ExcelImportTask> pagination = new ExcelImportTask().queryPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pagination.getContent())) {
            return pagination;
        }

        // 兼容早期 workbookName 不存储问题
        Map<Long, ExcelWorkbookDefinition> excelWorkbookDefinitionMap = new HashMap<>();
        List<ExcelImportTask> noWorkNameTasks = pagination.getContent().stream().filter(task -> StringUtils.isBlank(task.getWorkbookName())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(noWorkNameTasks)) {
            List<Long> workbookDefinitionIds = ListUtils.transform(noWorkNameTasks, ExcelImportTask::getWorkbookDefinitionId);
            if (CollectionUtils.isNotEmpty(workbookDefinitionIds)) {
                excelWorkbookDefinitionMap = FetchUtil.fetchMapByIds(ExcelWorkbookDefinition.class, workbookDefinitionIds);
            }
        }
        for (ExcelImportTask importTask : pagination.getContent()) {
            String name = importTask.getName();
            if (StringUtils.isNotBlank(name)) {
                if (name.startsWith(ExcelConstant.IMPORT_TASK_NAME)) {
                    name = name.substring(ExcelConstant.IMPORT_TASK_NAME.length());
                    importTask.setName(TranslateUtils.translateValues(ExcelConstant.IMPORT_TASK_NAME) + name);
                }
            }
            ExcelWorkbookDefinition workbookDefinition = excelWorkbookDefinitionMap.get(importTask.getWorkbookDefinitionId());
            if (workbookDefinition != null) {
                importTask.setWorkbookName(workbookDefinition.getName());
            }
        }

        return pagination;
    }

    @Action(displayName = "导入", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.TABLE})
    @Override
    public ExcelImportTask createImportTask(ExcelImportTask data) {
        return super.createImportTask(data);
    }

    @Action(displayName = "o2m导入", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.TABLE})
    public ExcelImportTask o2mImport(ExcelImportTask data) {
        innerTableImport(data, (excelImportContext, excelDataList) -> Ext.run(ExcelImportO2mDataExtPoint<Object>::importO2mData, excelImportContext, excelDataList));
        return data;
    }

    @Action(displayName = "m2m导入", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.TABLE})
    public ExcelImportTask m2mImport(ExcelImportTask data) {
        innerTableImport(data, (excelImportContext, excelDataList) -> Ext.run(ExcelImportM2mDataExtPoint<Object>::importM2mData, excelImportContext, excelDataList));
        return data;
    }

    private ExcelImportTask innerTableImport(ExcelImportTask data, BiFunction<ExcelImportContext, List<Object>, List<Object>> excelDataConverter) {
        List<Object> excelDataList = new ArrayList<>();
        final ExcelImportContext[] finalImportContext = {null};
        excelFileService.doImportByUrlTemporary(
                Optional.ofNullable(data.getFile()).map(PamirsFile::getUrl).orElse(null),
                data.getWorkbookDefinition(),
                () -> new DefaultExcelReadCallback() {
                    @Override
                    protected void call(ExcelImportContext importContext, ExcelAnalysisEventListener listener, Object data) {
                        finalImportContext[0] = importContext;
                        excelDataList.add(data);
                    }
                });

        ExcelImportContext excelImportContext = finalImportContext[0];
        if (excelImportContext == null) {
            // 没有数据
            return data;
        }
        String model = excelImportContext.getDefinitionContext().getSheetList().get(0).getBlockDefinitions().get(0).getBindingModel();

        List<Object> result = excelDataConverter.apply(excelImportContext, excelDataList);
        data.setImportDataList(
                result.stream().map(_i -> PamirsDataUtils.toJSONString(model, _i)).collect(Collectors.toList())
        );
        return data;
    }

    @Override
    protected void doImport(ExcelImportTask importTask, ExcelDefinitionContext context) {
        ExcelWorkbookDefinition workbookDefinition = importTask.getWorkbookDefinition();
        Boolean eachImport = workbookDefinition.getEachImport();
        boolean isSuccess;
        if (BooleanUtils.isFalse(eachImport)) {
            isSuccess = excelFileService.doImport(importTask, context, BatchImportExcelReadCallback::new);
        } else {
            isSuccess = excelFileService.doImport(importTask, context, DefaultExcelReadCallback::new);
        }
        if (!isSuccess) {
            if (PamirsSession.getMessageHub().isSuccess()) {
                PamirsSession.getMessageHub().error(I18nUtils.getMessage("pamirs.file.excel.import.error.checkRecord"));
            }
        }
    }
}
