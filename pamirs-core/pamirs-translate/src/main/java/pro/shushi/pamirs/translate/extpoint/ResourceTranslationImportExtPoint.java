package pro.shushi.pamirs.translate.extpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.enmu.ExcelTaskStateEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.manager.base.TranslateRedisManager;
import pro.shushi.pamirs.translate.task.ResourceTranslationImportTask;

import java.util.List;

/**
 * @author xzf 2023/1/12 17:06
 */
@Slf4j
@Component
@Ext(ExcelImportTask.class)
@Deprecated
public class ResourceTranslationImportExtPoint extends AbstractExcelImportDataExtPointImpl<List<ResourceTranslationItem>> {

    @Autowired
    private TranslateRedisManager         translateRedisManager;
    @Autowired
    private ResourceTranslationImportTask resourceTranslationImportTask;

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.model==\"" + ResourceTranslation.MODEL_MODEL + "\"",priority = 999)
    public Boolean importData(ExcelImportContext importContext, List<ResourceTranslationItem> data) {
        ExcelImportTask importTask = importContext.getImportTask();
        try {
            if (!translateRedisManager.canLockImportKey()) {
                importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "有导入任务正在执行，请过5分钟后再试");
                throw PamirsException.construct(TranslateEnumerate.CURRENT_HAS_IMPORT_TASK_EXECUTE_ERROR).errThrow();
            }
            translateRedisManager.putExcelSnapshot(importTask.getId(), data);
            resourceTranslationImportTask.createStatusCheckTask(importTask.getId());

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
}
