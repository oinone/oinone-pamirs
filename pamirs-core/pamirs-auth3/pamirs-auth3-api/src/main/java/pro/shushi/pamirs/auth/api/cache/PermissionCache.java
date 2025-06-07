package pro.shushi.pamirs.auth.api.cache;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthL2Cache;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.meta.api.CommonApiFactory;

/**
 * 权限缓存清理
 *
 * @author Adamancy Zhang at 10:19 on 2024-01-24
 */
@Component
public class PermissionCache {

    public void clear() {
        CommonApiFactory.getApi(AuthRoleSession.class).clear();
        CommonApiFactory.getApi(AuthL2Cache.class).clear();
    }
}
