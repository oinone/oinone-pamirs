package pro.shushi.pamirs.file.api.util;

import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * Excel导出帮助类
 *
 * @author Adamancy Zhang at 15:07 on 2024-04-01
 */
public class ExcelExportHelper {

    private ExcelExportHelper() {
        //reject create object
    }

    public static boolean fetchClearExportStyle(ExcelWorkbookDefinition workbookDefinition) {
        FileProperties.FileExportProperties exportProperties = BeanDefinitionUtils.getBean(FileProperties.class).getExportProperty();
        Boolean clearExportStyle = workbookDefinition.getClearExportStyle();
        if (clearExportStyle == null) {
            clearExportStyle = exportProperties.getDefaultClearExportStyle();
        }
        return clearExportStyle;
    }

    public static int fetchMaxSupportLength(ExcelWorkbookDefinition workbookDefinition, boolean clearExportStyle) {
        FileProperties.FileExportProperties exportProperties = BeanDefinitionUtils.getBean(FileProperties.class).getExportProperty();
        Integer maxSupportLength;
        if (clearExportStyle) {
            maxSupportLength = workbookDefinition.getCsvMaxSupportLength();
        } else {
            maxSupportLength = workbookDefinition.getExcelMaxSupportLength();
        }
        if (maxSupportLength == null || maxSupportLength <= 0) {
            if (clearExportStyle) {
                maxSupportLength = exportProperties.getCsvMaxSupportLength();
            } else {
                maxSupportLength = exportProperties.getExcelMaxSupportLength();
            }
        }
        return maxSupportLength;
    }

    public static void addUnsupportedErrorMessage(ExcelExportTask exportTask, int maxSupportLength, boolean clearExportStyle) {
        if (clearExportStyle) {
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, I18nUtils.getMessage("ExcelExportHelper.csv_max_support_length", maxSupportLength));
        } else {
            exportTask.addTaskMessage(TaskMessageLevelEnum.ERROR, I18nUtils.getMessage("ExcelExportHelper.excel_max_support_length", maxSupportLength));
        }
    }
}
