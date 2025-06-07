package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheWithDoubleKey;

/**
 * 事务配置缓存接口
 *
 * <p>key为namespace.fun
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface TxConfigCacheApi extends SessionCacheWithDoubleKey<TxConfig>, SessionCacheInitApi {

}
