package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheWithDoubleKey;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;

import java.util.List;

/**
 * 扩展点实现缓存接口
 *
 * <p>key为namespace.name
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ExtPointImplementationCacheApi extends SessionCacheWithDoubleKey<List<ExtPointImplementation>>, SessionCacheInitApi {

}
