package pro.shushi.pamirs.boot.base.constants;

/**
 * 视图常量
 * <p>
 * 2020/11/16 7:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ViewConstants {

    /**
     * 优先级
     */
    int manualPriority = 77;

    /**
     * 默认优先级
     */
    int defaultPriority = 88;

    /**
     * 子视图名称后缀
     */
    String subViewNameSuffix = "From";

    /**
     * 英文名
     */
    interface Name {

        // 表单视图
        String formView = "formView";

        // 详情视图
        String detailView = "detailView";

        // 表格视图
        String tableView = "tableView";

        // 表格对话框视图
        String dialogTableView = "dialogTableView";

        // 表单对话框视图
        String dialogFormView = "dialogFormView";

    }

    /**
     * 显示名称
     */
    interface DisplayName {

        // 表单视图
        String formView = "ViewConstants.DisplayName.formView";

        // 详情视图
        String detailView = "ViewConstants.DisplayName.detailView";

        // 表格视图
        String tableView = "ViewConstants.DisplayName.tableView";

        // 表格对话框视图
        String dialogTableView = "ViewConstants.DisplayName.dialogTableView";

        // 表单对话框视图
        String dialogFormView = "ViewConstants.DisplayName.dialogFormView";

    }

    /**
     * 扩展数据key
     */
    interface Extension {

        // 个性化配置
        String userPreference = "userPreference";

        // ai agent
        String aiPreference = "aiPreference";

    }
}
