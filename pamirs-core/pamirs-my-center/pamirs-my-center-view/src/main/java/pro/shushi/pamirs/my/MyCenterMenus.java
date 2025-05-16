package pro.shushi.pamirs.my;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.my.pmodel.MyPamirsUserProxy;

@UxMenus
class MyCenterMenus implements ViewActionConstants {
    /**
     * topBar入口,菜单名称不要改,需要指定module
     */
    @UxMenu("个人设置")
    @UxRoute(model = MyPamirsUserProxy.MODEL_MODEL, module = MyCenterModule.MODULE_MODULE, viewName = "MyPamirsUserProxy_Setting_Form")
    class SettingMenu {
    }
}