package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.session.cache.SessionCache;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;

/**
 * 模块缓存接口
 *
 * <p>key为code
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SequenceConfigCacheApi extends SessionCache<SequenceConfig>, SessionCacheInitApi {

}
