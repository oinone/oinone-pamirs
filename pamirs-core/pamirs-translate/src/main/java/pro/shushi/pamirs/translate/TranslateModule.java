package pro.shushi.pamirs.translate;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.resource.api.ResourceModule;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;

/**
 * TranslateModule
 *
 * @author yakir on 2020/05/11 12:10.
 */
@Slf4j
@Component
@Module(
        name = TranslateModule.MODULE_NAME,
        displayName = "翻译",
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE, ResourceModule.MODULE_MODULE,
                FileModule.MODULE_MODULE}
)
@Module.module(TranslateModule.MODULE_MODULE)
@Module.Advanced(
        selfBuilt = true
)
@UxHomepage(value = @UxRoute(value = ResourceTranslation.MODEL_MODEL), actionName = "TranslationMenus_ResourceTranslationMenu")
public class TranslateModule implements PamirsModule {

    public static final String MODULE_MODULE = "translate";

    public static final String MODULE_NAME = "translate";

    @Override
    public String[] packagePrefix() {

        log.info("Pamirs Translate Module .....");

        return new String[]{
                "pro.shushi.pamirs.translate"
        };
    }
}
