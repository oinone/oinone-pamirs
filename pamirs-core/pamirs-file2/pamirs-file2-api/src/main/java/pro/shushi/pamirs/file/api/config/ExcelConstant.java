package pro.shushi.pamirs.file.api.config;

/**
 * Excel常量
 *
 * @author Adamancy Zhang at 21:22 on 2024-08-05
 */
public interface ExcelConstant {

    String TEMPLATE_IS_NULL = "未找到正确的导出模板，无法执行导出";

    String DEFAULT_ERROR_MESSAGE = "执行导入任务时出现异常";

    String DEFAULT_ERROR_FILE_SUFFIX = "-错误信息";

    String IMPORT_NAME = "导入";

    String IMPORT_TASK_NAME = "【导入】";

    String EXPORT_NAME = "导出";

    String EXPORT_TASK_NAME = "【导出】";

    /**
     * 【选择字段导出】使用的模版名称
     */
    String SELECT_FIELD_AUTOMATIC_TEMPLATE = "automatic";

}
