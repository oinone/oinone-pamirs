package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheWithDoubleKey;

/**
 * 孤立函数缓存接口
 *
 * <p>key为namespace#fun
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface StandaloneFunCacheApi extends SessionCacheWithDoubleKey<Function>, SessionCacheInitApi {

}
