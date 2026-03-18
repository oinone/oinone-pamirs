package pro.shushi.pamirs.file.api.config;

import pro.shushi.pamirs.core.common.constant.CommonConstants;

/**
 * Excel常量
 *
 * @author Adamancy Zhang at 21:22 on 2024-08-05
 */
public interface ExcelConstant {

    String TEMPLATE_IS_NULL = "pamirs.file.excel.template.isNull";

    String DEFAULT_ERROR_MESSAGE = "pamirs.file.excel.error.default";

    String DEFAULT_ERROR_FILE_SUFFIX = "pamirs.file.excel.error.fileSuffix";

    String IMPORT_NAME = "pamirs.file.excel.name.import";

    String IMPORT_TASK_NAME = "pamirs.file.excel.task.name.import";

    String IMPORT_TASK_NAME_TRANSLATE = "【" + CommonConstants.TRANSLATE_PREFIX + IMPORT_NAME + CommonConstants.TRANSLATE_SUFFIX + "】";

    String EXPORT_NAME = "pamirs.file.excel.name.export";

    String EXPORT_TASK_NAME = "pamirs.file.excel.task.name.export";

    String EXPORT_TASK_NAME_TRANSLATE = "【" + CommonConstants.TRANSLATE_PREFIX + EXPORT_NAME + CommonConstants.TRANSLATE_SUFFIX + "】";

    /**
     * 【选择字段导出】使用的模版名称
     */
    String SELECT_FIELD_AUTOMATIC_TEMPLATE = "automatic";

}
