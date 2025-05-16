package pro.shushi.pamirs.user.view;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.user.api.model.PamirsUser;

/**
 * Business管理后台菜单
 * <p>
 * 2020/11/18 5:08 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings({"unused", "InnerClassMayBeStatic"})// @formatter:off 忽略格式化，请开启IDE格式化注解开关

@UxMenus(module = CommonConstants.MANAGEMENT_CENTER_MODULE, basePriority = 100) /*可以注解到该模块的任意类上，建议同一个模块中只配置一处*/ class UserMenus implements ViewActionConstants {
    @UxMenu("用户") @UxRoute(model = PamirsUser.MODEL_MODEL) class PamirsUserMenu {}
}
