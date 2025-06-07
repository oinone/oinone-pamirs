package pro.shushi.pamirs.user.core.ext;

import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.base.extpoint.DeleteAfterExtPoint;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

/**
 * @author shier
 * date  2022/6/29 下午6:14
 */
@Ext(PamirsUser.class)
public class PamirsUserDeleteExtpoint implements DeleteAfterExtPoint<PamirsUser> {

    @Override
    @ExtPoint.Implement(displayName = "删除用户的后置扩展点")
    public List<PamirsUser> deleteAfter(List<PamirsUser> data) {
        for (PamirsUser user : data) {
            UserCache.clearSessionByUid(user.getId());
        }
        return data;
    }
}
