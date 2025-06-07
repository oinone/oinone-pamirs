package pro.shushi.pamirs.boot.base.constants;

/**
 * 视图动作常量
 * <p>
 * 2020/11/16 7:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ViewActionConstants {

    /**
     * 首页
     */
    interface homepage {
        /**
         * 英文名
         */
        String name = "homepage";
        /**
         * 显示名称
         */
        String displayName = "首页";
        /**
         * 标题
         */
        String title = "首页";
    }

    /**
     * 列表页面
     */
    interface redirectTablePage {
        /**
         * 英文名
         */
        String name = "redirectTablePage";
        /**
         * 显示名称
         */
        String displayName = "表格";
        /**
         * 标题
         */
        String title = "列表";
    }

    /**
     * 创建页面
     */
    interface redirectCreatePage {
        /**
         * 英文名
         */
        String name = "redirectCreatePage";
        /**
         * 显示名称
         */
        String displayName = "创建";
        /**
         * 标题
         */
        String title = "创建";
    }

    /**
     * 更新页面
     */
    interface redirectUpdatePage {
        /**
         * 英文名
         */
        String name = "redirectUpdatePage";
        /**
         * 显示名称
         */
        String displayName = "编辑";
        /**
         * 标题
         */
        String title = "编辑";
    }

    /**
     * 详情页面
     */
    interface redirectDetailPage {
        /**
         * 英文名
         */
        String name = "redirectDetailPage";
        /**
         * 显示名称
         */
        String displayName = "详情";
        /**
         * 标题
         */
        String title = "详情";
    }

    /**
     * 内嵌视图创建页面
     */
    interface O2MCreate {
        /**
         * 英文名
         */
        String name = "internalGotoO2MCreateDialog";
        /**
         * 显示名称
         */
        String displayName = "内嵌视图创建";
        /**
         * 标题
         */
        String title = "创建";
        /**
         * 类型
         */
        String type = "primary";
    }


    /**
     * 一对多编辑按钮
     */
    interface O2MEdit {
        /**
         * 英文名
         */
        String name = "internalGotoO2MEditDialog";
        /**
         * 显示名称
         */
        String displayName = "内嵌视图创建";
        /**
         * 标题
         */
        String title = "编辑";
        /**
         * 类型
         */
        String type = "primary";
    }

    /**
     * 多对多添加按钮
     */
    interface M2MCreate {
        /**
         * 英文名
         */
        String name = "internalGotoM2MListDialog";
        /**
         * 显示名称
         */
        String displayName = "内嵌视图添加";
        /**
         * 标题
         */
        String title = "添加";
        /**
         * 类型
         */
        String type = "primary";
    }

    /**
     * 导入按钮
     */
    interface Import {
        String name = "internalGotoListImportDialog";
        String displayName = "导入";
        String title = "导入";
        String type = "default";
    }

    /**
     * 导出按钮
     */
    interface Export {
        String name = "internalGotoListExportDialog";
        String displayName = "导出";
        String title = "导出";
        String type = "default";
    }

    /**
     * 打印按钮
     */
    interface Print {
        String name = "internalGotoPrintDialog";
        String displayName = "打印";
        String title = "打印";
        String type = "default";
    }
}
