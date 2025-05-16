package pro.shushi.pamirs.translate.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.action.AbstractExcelImportTaskAction;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.translate.template.ChangeTranslateTemplate;
import pro.shushi.pamirs.translate.tmodel.TranslationItemChange;

@Component
@Model.model(TranslationItemChange.MODEL_MODEL)
public class TranslationItemChangeAction extends AbstractExcelImportTaskAction<ExcelImportTask> {

    public TranslationItemChangeAction(FileProperties fileProperties, ExcelFileService excelFileService) {
        super(fileProperties, excelFileService);
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "更改翻译文件 初始化")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public TranslationItemChange construct(TranslationItemChange data) {
        ExcelWorkbookDefinition excelWorkbookDefinition = new ExcelWorkbookDefinition();
        excelWorkbookDefinition.setName(ChangeTranslateTemplate.TEMPLATE_NAME);
        ExcelWorkbookDefinition workbookDefinition = excelWorkbookDefinition.queryOne();
        data.setWorkbookId(workbookDefinition.getId());
        return data.construct();
    }

    @Action(displayName = "导入更改翻译应用范围", bindingType = {ViewTypeEnum.FORM}, contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Action.Advanced(type = {FunctionTypeEnum.CREATE, FunctionTypeEnum.UPDATE})
    public TranslationItemChange importTranslationChange(TranslationItemChange data) {
        ExcelWorkbookDefinition excelWorkbookDefinition = new ExcelWorkbookDefinition();
        excelWorkbookDefinition.setId(data.getWorkbookId());
        ExcelImportTask excelImportTask = (ExcelImportTask) new ExcelImportTask().setWorkbookDefinition(excelWorkbookDefinition);
        excelImportTask.setFile(data.getFile());
        super.createImportTask(excelImportTask);
        return data;
    }

    @Override
    protected void doImport(ExcelImportTask importTask, ExcelDefinitionContext context) {
        excelFileService.doImportAsync(importTask, context);
    }
}
