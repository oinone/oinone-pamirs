package pro.shushi.pamirs.translate.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.file.api.action.AbstractExcelImportTaskAction;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.template.TranslateTemplate;
import pro.shushi.pamirs.translate.tmodel.TranslationItemImport;

@Component
@Model.model(TranslationItemImport.MODEL_MODEL)
public class TranslationItemImportAction extends AbstractExcelImportTaskAction<ExcelImportTask> {

    public TranslationItemImportAction(FileProperties fileProperties, ExcelFileService excelFileService) {
        super(fileProperties, excelFileService);
    }


    @Function(openLevel = FunctionOpenEnum.API, summary = "导入翻译文件 初始化")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public TranslationItemImport construct(TranslationItemImport data) {
        ExcelWorkbookDefinition excelWorkbookDefinition = new ExcelWorkbookDefinition();
        excelWorkbookDefinition.setName(TranslateTemplate.TEMPLATE_NAME);
        ExcelWorkbookDefinition workbookDefinition = excelWorkbookDefinition.queryOne();
        data.setWorkbookId(workbookDefinition.getId());
        return data.construct();
    }


    @Action(displayName = "导入翻译文件", bindingType = {ViewTypeEnum.FORM}, contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Action.Advanced(type = FunctionTypeEnum.CREATE)
    public TranslationItemImport importTranslation(TranslationItemImport data) {
        PamirsFile file = data.getFile();
        if (file != null) {
            Long kbSize = file.getSize();
            double mbSize = convertKBToMB(kbSize);
            if (mbSize > 50) {
                throw PamirsException.construct(TranslateEnumerate.FILE_SIZE_LIMIT_EXCEEDED).errThrow();
            }
        }
        ExcelWorkbookDefinition excelWorkbookDefinition = new ExcelWorkbookDefinition();
        excelWorkbookDefinition.setId(data.getWorkbookId());
        ExcelImportTask excelImportTask = (ExcelImportTask) new ExcelImportTask().setWorkbookDefinition(excelWorkbookDefinition);
        excelImportTask.setFile(data.getFile());
        excelImportTask.setMaxErrorLength(-1);
        super.createImportTask(excelImportTask);
        return data;
    }

    @Override
    protected void doImport(ExcelImportTask importTask, ExcelDefinitionContext context) {
        excelFileService.doImportAsync(importTask, context);
    }

    /**
     * KB 转 MB
     *
     * @param kbSize
     * @return
     */
    public static double convertKBToMB(double kbSize) {
        return kbSize / (1024.0 * 1024.0); // 1 MB = 1024 KB
    }


}
