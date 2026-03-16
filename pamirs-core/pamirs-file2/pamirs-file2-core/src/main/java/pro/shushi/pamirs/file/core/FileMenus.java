package pro.shushi.pamirs.file.core;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;

@UxMenus /*可以注解到该模块的任意类上，建议同一个模块中只配置一处*/
class FileMenus implements ViewActionConstants {

    @UxMenu("导入任务")
    @UxRoute(model = ExcelImportTask.MODEL_MODEL, viewName = "ExcelImportTaskTable", module = FileModule.MODULE_MODULE)
    class PamirsFileExportMenu {
    }

    @UxMenu("导出任务")
    @UxRoute(model = ExcelExportTask.MODEL_MODEL, viewName = "ExcelExportTaskTable")
    class PamirsFileImportMenu {
    }

//    @UxMenu("导入/导出模板")
//    @UxRoute(model = ExcelWorkbookDefinition.MODEL_MODEL, viewName = "ExcelWorkbookDefinitionTable")
//    class WorkbookDefinitionMenu {
//    }
}
