package pro.shushi.pamirs.translate.init;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.translate.proxy.TranslationItemExportProxy;
import pro.shushi.pamirs.translate.proxy.TranslationItemProxy;
import pro.shushi.pamirs.translate.tmodel.TranslationItemChange;
import pro.shushi.pamirs.translate.tmodel.TranslationItemImport;

@UxMenus
public class TranslationMenus implements ViewActionConstants {

    @UxMenu("翻译")
    @UxRoute(model = ResourceTranslation.MODEL_MODEL, viewName = "translationManage")
    class ResourceTranslationMenu {

    }
}
