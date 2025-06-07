package pro.shushi.pamirs.file.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.resource.api.ResourceModule;

@UxHomepage(actionName = "FileMenus_PamirsFileExportMenu", value = @UxRoute(ExcelImportTask.MODEL_MODEL))
@Component
@Boot
@Module(
        name = FileModule.MODULE_NAME,
        displayName = "文件",
        version = "5.0.0",
        dependencies = {ResourceModule.MODULE_MODULE}
)
@Module.module(FileModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true)
public class FileModule implements PamirsModule {

    public static final String MODULE_MODULE = "file";

    public static final String MODULE_NAME = "file";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.file",
                "pro.shushi.pamirs.framework.connectors.cdn.enmu",
        };
    }

}
