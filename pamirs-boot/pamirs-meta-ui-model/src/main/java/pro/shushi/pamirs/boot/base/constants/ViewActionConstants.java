package pro.shushi.pamirs.boot.base.constants;

/**
 * 视图动作常量
 * <p>
 *     <ul>
 *         <li>表格表头动作：创建(55) | 删除(66) | 导入(77) | 导出(78) | 打印(88)</li>
 *         <li>表格行内动作：详情(66) | 编辑(77)</li>
 *         <li>表单/详情表头动作：返回(99) | 创建(100) | 更新(100)</li>
 *     </ul>
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
        String displayName = "ViewActionConstants.homepage.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.homepage.title";
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
        String displayName = "ViewActionConstants.redirectTablePage.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.redirectTablePage.title";
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
        String displayName = "ViewActionConstants.redirectCreatePage.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.redirectCreatePage.title";
        /**
         * 优先级
         */
        int priority = 55;
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
        String displayName = "ViewActionConstants.redirectUpdatePage.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.redirectUpdatePage.title";
        /**
         * 优先级
         */
        int priority = 77;
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
        String displayName = "ViewActionConstants.redirectDetailPage.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.redirectDetailPage.title";
        /**
         * 优先级
         */
        int priority = 66;
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
        String displayName = "ViewActionConstants.O2MCreate.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.O2MCreate.title";
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
        String displayName = "ViewActionConstants.O2MEdit.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.O2MEdit.title";
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
        String displayName = "ViewActionConstants.M2MCreate.displayName";
        /**
         * 标题
         */
        String title = "ViewActionConstants.M2MCreate.title";
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
        String displayName = "ViewActionConstants.Import.displayName";
        String title = "ViewActionConstants.Import.title";
        String type = "default";
        /**
         * 优先级
         */
        int priority = 77;
    }

    /**
     * 导出按钮
     */
    interface Export {
        String name = "internalGotoListExportDialog";
        String displayName = "ViewActionConstants.Export.displayName";
        String title = "ViewActionConstants.Export.title";
        String type = "default";
        /**
         * 优先级
         */
        int priority = 78;
    }

    /**
     * 打印按钮
     */
    interface Print {
        String name = "internalGotoPrintDialog";
        String displayName = "ViewActionConstants.Print.displayName";
        String title = "ViewActionConstants.Print.title";
        String type = "default";
        /**
         * 优先级
         */
        int priority = 88;
    }
}
