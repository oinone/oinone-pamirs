package pro.shushi.pamirs.user.view.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.spi.api.UserPreferenceService;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.user.api.model.UserListFieldPreferStore;

@Component
@Order(0)
@SPI.Service
public class DefaultUserPreferenceServiceImpl implements UserPreferenceService {

    @Override
    public String load(ViewAction viewAction, View view) {
        if (view == null) {
            return null;
        }
        if (!ViewTypeEnum.TABLE.equals(view.getType())) {
            // 只有table支持个性化配置, 其他不查询
            return null;
        }
        if (viewAction != null && !(ActionTargetEnum.ROUTER.equals(viewAction.getTarget()) || ActionTargetEnum.OPEN_WINDOW.equals(viewAction.getTarget()))) {
            // 内嵌的页面不支持个性化配置, 不查询
            return null;
        }
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            return null;
        }
        UserListFieldPreferStore userListFieldPreferStore = new UserListFieldPreferStore().setViewName(view.getName()).setResModel(view.getModel()).setPartnerId(userId).queryOne();
        if (userListFieldPreferStore == null) {
            return null;
        }
        // 前端序列化
        return PamirsDataUtils.toJSONString(UserListFieldPreferStore.MODEL_MODEL, userListFieldPreferStore);
    }
}
