package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.api.HooksCacheApi;
import pro.shushi.pamirs.meta.domain.fun.Hook;

/**
 * 拦截器缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class HooksCache extends AbstractListCache<Hook> implements HooksCacheApi {

    @Override
    public String type() {
        return Hook.class.getSimpleName();
    }

}
