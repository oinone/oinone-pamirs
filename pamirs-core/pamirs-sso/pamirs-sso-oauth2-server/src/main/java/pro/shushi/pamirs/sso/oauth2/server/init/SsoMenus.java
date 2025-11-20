package pro.shushi.pamirs.sso.oauth2.server.init;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.sso.api.model.SsoClient;

@UxMenus
public class SsoMenus implements ViewActionConstants {

    @UxMenu("单点登录管理")
    class SsoMenu {
        @UxMenu("客户端信息")
        @UxRoute(model = SsoClient.MODEL_MODEL)
        class SsoClientMenu {
        }

    }
}
