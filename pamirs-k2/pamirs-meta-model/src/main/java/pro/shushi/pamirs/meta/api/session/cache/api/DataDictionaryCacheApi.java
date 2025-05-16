package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.session.cache.SessionCache;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

/**
 * 数据字典缓存接口
 *
 * <p>key为dictionary
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface DataDictionaryCacheApi extends SessionCache<DataDictionary>, SessionCacheInitApi {

}
