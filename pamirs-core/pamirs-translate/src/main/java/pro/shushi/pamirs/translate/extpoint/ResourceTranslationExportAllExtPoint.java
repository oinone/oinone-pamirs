package pro.shushi.pamirs.translate.extpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.extpoint.impl.DefaultExcelExportFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.enmu.TranslateEnumerate;
import pro.shushi.pamirs.translate.manager.base.TranslateCoreManager;
import pro.shushi.pamirs.translate.manager.base.TranslateRedisManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-11-04 18:09
 */
@Slf4j
@Component
@Ext(ExcelExportTask.class)
@Deprecated
public class ResourceTranslationExportAllExtPoint extends DefaultExcelExportFetchDataExtPoint {

    @Autowired
    private TranslateRedisManager translateRedisManager;
    @Autowired
    private TranslateCoreManager translateCoreManager;

    @Override
    @ExtPoint.Implement(expression = "context.name==\"全部翻译项导出\" && context.model==\"" + ResourceTranslation.MODEL_MODEL + "\"")
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        List<Object> objects = new ArrayList<>();
        try {
            if (!translateRedisManager.canLockExportKey()) {
                throw PamirsException.construct(TranslateEnumerate.CURRENT_HAS_EXPORT_TASK_EXECUTE_ERROR)
                        .errThrow();
            }
            log.info("开始导出全部翻译项");
            List<ResourceTranslationItem> result = translateCoreManager.exportAll();
            objects.add(result);
            log.info("全部翻译项导出成功,共计：【{}】 条", result.size());
        } catch (Exception e) {
            log.error("全部翻译项导出失败，异常信息", e);
        } finally {
            translateRedisManager.delLockExportKey();
        }
        return objects;
    }

    @Override
    protected IWrapper<?> queryBefore(ExcelExportFetchDataContext context, IWrapper<?> wrapper) {
        //不走机制，自己处理数据
        return null;
    }
}
