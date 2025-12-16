package pro.shushi.pamirs.channel.core.init;

import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.channel.model.ChannelModel;

/**
 * ChannelMenus
 *
 * @author yakir on 2025/10/24 14:49.
 */
@UxMenus
final class ChannelMenus {

    @UxMenu("增强模型列表")
    @UxRoute(model = ChannelModel.MODEL_MODEL, viewName = "tableView")
    static class ChannelModelMenu {}
}
