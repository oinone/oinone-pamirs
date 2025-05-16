package pro.shushi.pamirs.meta.api.session.cache.util;

import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Map;

/**
 * session缓存工具类
 * <p>
 * 2022/5/6 11:41 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SessionCacheUtil {

    public static <T extends SessionCacheInitApi, TT extends T> void initCache(
            Map<String, Object> extendCacheMap,
            Class<T> apiClass, Class<TT> initializeClass) {
        SessionCacheInitApi cacheApi = TypeUtils.getNewInstance(initializeClass);
        cacheApi.init();
        extendCacheMap.putIfAbsent(apiClass.getSimpleName(), cacheApi);
    }

}
