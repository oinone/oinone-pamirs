package pro.shushi.pamirs.boot.base.ux.cache.util;

import pro.shushi.pamirs.meta.api.session.cache.SessionCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存工具类
 * <p>
 * 2022/5/5 4:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class UiSessionCacheUtil {

    public static <T> void putDataToListCache(SessionCache<List<T>> cacheApi, String key, T data) {
        cacheApi.putIfAbsent(key, new ArrayList<>());
        List<T> dataList = cacheApi.get(key);
        dataList.add(data);
    }

}
