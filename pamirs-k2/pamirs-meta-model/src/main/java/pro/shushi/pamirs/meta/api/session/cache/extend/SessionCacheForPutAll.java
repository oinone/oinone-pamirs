package pro.shushi.pamirs.meta.api.session.cache.extend;

import java.util.Map;

/**
 * session本地缓存putAll接口
 * <p>
 * 2021/8/19 1:00 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SessionCacheForPutAll<T> {

    void putAll(Map<String, T> map);

}
