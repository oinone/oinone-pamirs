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
        /**
         * topBar入口,菜单名称不要改,需要指定module
         */
        @UxMenu("登录页配置")
        @UxRoute(model = GlobalAppConfigProxy.MODEL_MODEL, module = SysSettingModule.MODULE_MODULE, viewName = "GlobalAppConfigProxy_Login_Form")
        class LoginMenu {
        }

        @UxMenu("企业形象配置")
        @UxRoute(model = GlobalAppConfigProxy.MODEL_MODEL, module = SysSettingModule.MODULE_MODULE, viewName = "GlobalAppConfigProxy_CorporateImage_Form")
        class CorporateImageMenu {
        }

        @UxMenu("系统风格配置")
        @UxRoute(model = GlobalAppConfigProxy.MODEL_MODEL, module = SysSettingModule.MODULE_MODULE, viewName = "GlobalAppConfigProxy_SysStyle_Form")
        class SysStyleMenu {
        }

        @UxMenu("高级首页配置")
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