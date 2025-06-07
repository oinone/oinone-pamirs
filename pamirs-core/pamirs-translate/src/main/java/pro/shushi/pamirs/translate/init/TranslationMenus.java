package pro.shushi.pamirs.translate.init;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;

@UxMenus
public class TranslationMenus implements ViewActionConstants {

    @UxMenu("翻译")
    @UxRoute(model = ResourceTranslation.MODEL_MODEL, viewName = "translationManage")
    class ResourceTranslationMenu {

    }
}
