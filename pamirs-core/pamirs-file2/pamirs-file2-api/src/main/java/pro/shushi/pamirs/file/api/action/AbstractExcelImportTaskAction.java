package pro.shushi.pamirs.file.api.action;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.ExcelTaskStateEnum;
import pro.shushi.pamirs.file.api.enmu.FileExpEnumerate;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.util.EasyExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelWorkbookDefinitionUtil;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.file.api.util.ResourceFileHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Optional;
/**
 * @author Adamancy Zhang
 * @date 2020-11-10 12:25
 */
@Slf4j

public abstract class AbstractExcelImportTaskAction<T extends ExcelImportTask> {

    protected ExcelFileService excelFileService;

    private FileProperties fileProperties;
    public AbstractExcelImportTaskAction(FileProperties fileProperties, ExcelFileService excelFileService) {
        this.fileProperties = fileProperties;
        this.excelFileService = excelFileService;
    }

    public FileProperties getFileProperties() {
        return fileProperties;
    }

    public void setFileProperties(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    public T createImportTask(T data) {
        try {
            ExcelWorkbookDefinition workbookDefinition = FetchUtil.fetchOne(data.getWorkbookDefinition());
            if (workbookDefinition == null) {
                throw PamirsException.construct(FileExpEnumerate.IMPORT_TEMPLATE_NOT_EXIST).errThrow();
            }
            String fileUrl = Optional.ofNullable(data.getFile()).map(PamirsFile::getUrl).orElse(null);
            if (StringUtils.isBlank(fileUrl)) {
                throw PamirsException.construct(FileExpEnumerate.FILE_NOT_EXIST).errThrow();
            }

            ExcelDefinitionContext definitionContext = excelFileService.refreshDefinitionContext(workbookDefinition);
            definitionContext.setCurrentLang(TranslateServiceHolder.get().getCurrentLang());

            ExcelWorkbookDefinitionUtil.initImportTask(definitionContext, workbookDefinition, data);

            try {
                doImport(data, definitionContext);
            } catch (Throwable t) {
                log.error("Unpredictable exception occurred during data import", t);
                data.setState(ExcelTaskStateEnum.FAILURE);
                data.addTaskMessage(TaskMessageLevelEnum.ERROR, EasyExcelHelper.getErrorMessage(t));
                data.updateById();
            }
        } catch (Throwable e) {
            log.error("doImport error.", e);
            if (PamirsSession.getMessageHub().isSuccess()) {
                PamirsSession.getMessageHub().info(I18nUtils.getMessage("pamirs.file.excel.import.exception"));
            }
        }

        return data;
    }

    protected abstract void doImport(T importTask, ExcelDefinitionContext context);
}
