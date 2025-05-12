package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.session.cache.SessionListCache;
import pro.shushi.pamirs.meta.domain.fun.Hook;

/**
 * 拦截器缓存接口
 *
 * <p>key为Hook
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface HooksCacheApi extends SessionListCache<Hook> {

}
