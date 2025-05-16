package pro.shushi.pamirs.user.view.action;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.user.api.model.UserListFieldPreferStore;


@Base
@Model.model(UserListFieldPreferStore.MODEL_MODEL)
public class UserListFieldPreferStoreAction {

    @Action(displayName = "保存用户视图字段偏好")
    @Action.Advanced(invisible = "true")
    public UserListFieldPreferStore save(UserListFieldPreferStore data) {
        if (null == data) {
            return data;
        }
        if (StringUtils.isNotBlank(data.getResModel()) && StringUtils.isNotBlank(data.getViewName())) {
            if (data.getPartnerId() == null) {
                data.setPartnerId(PamirsSession.getUserId());
            }
            data.createOrUpdate();
        }
        return data;
    }
}
