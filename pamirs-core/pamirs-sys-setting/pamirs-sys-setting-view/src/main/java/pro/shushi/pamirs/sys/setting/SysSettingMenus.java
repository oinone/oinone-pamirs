package pro.shushi.pamirs.sys.setting;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.sys.setting.pmodel.GlobalAppConfigProxy;

@UxMenus
class SysSettingMenus implements ViewActionConstants {
    @UxMenu("全局配置")
    class GlobalMenu {

        @UxMenu(label = "系统风格配置", priority = 50)
        @UxRoute(model = GlobalAppConfigProxy.MODEL_MODEL, module = SysSettingModule.MODULE_MODULE, viewName = "GlobalAppConfigProxy_SysStyle_Form")
        class SysStyleMenu {
        }

        @UxMenu(label = "高级首页配置", priority = 51)
        @UxRoute(model = GlobalAppConfigProxy.MODEL_MODEL, viewName = "advanced_home_page_configuration")
        class AdvancedHomePageConfigMenu {
        }
    }

    @UxMenu("应用配置")
    class ApplicationMenu {

        @UxMenu("翻译管理配置")
        @UxRoute(model = GlobalAppConfigProxy.MODEL_MODEL, viewName = "SysSettingMenus_GlobalMenu_TranslateMenu")
        class GlobalTranslationManageMenu {

        }
    }
}