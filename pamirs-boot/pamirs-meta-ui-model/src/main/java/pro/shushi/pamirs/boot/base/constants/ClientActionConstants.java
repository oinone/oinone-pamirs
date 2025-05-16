package pro.shushi.pamirs.boot.base.constants;

/**
 * 客户端动作常量
 * <p>
 * 2022/5/3 5:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ClientActionConstants {

    /**
     * 返回按钮
     */
    interface GoBack {
        String name ="internalGotoListTableRouter";
        String fun = "$$internal_GotoListTableRouter";
        String label = "返回";
        String propNameType = "type";
        String propValueType = "default";
    }

    /**
     * 内嵌视图删除按钮
     */
    interface X2MDelete {
        String name ="internalDeleteOne";
        String fun = "$$internal_DeleteOne";
        String label = "删除";
        String propNameType = "type";
        String propValueType = "primary";
    }

    /**
     * 导入按钮
     */
    interface Import {
        String name ="$$internal_GotoListImportDialog";
        String fun = "$$internal_GotoListImportDialog";
        String label = "导入";
        String propNameType = "type";
        String propValueType = "default";
    }

    /**
     * 导出按钮
     */
    interface Export {
        String name ="$$internal_GotoListExportDialog";
        String fun = "$$internal_GotoListExportDialog";
        String label = "导出";
        String propNameType = "type";
        String propValueType = "default";
    }

    /**
     * 表格添加一行
     */
    interface TableAddRow {
        String name ="$$internal_AddOne";
        String fun = "$$internal_AddOne";
        String label = "插入";
        String propNameType = "type";
        String propValueType = "default";
    }

    /**
     * 表格复制一行
     */
    interface TableCopyRow {
        String name ="$$internal_CopyOne";
        String fun = "$$internal_CopyOne";
        String label = "复制";
        String propNameType = "type";
        String propValueType = "default";
    }
}
