package pro.shushi.pamirs.meta.api.core.orm.convert;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据转换API
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
public interface ReentryApi {

    ThreadLocal<Map<Integer, SoftReference<Object>>> reentryMap = new ThreadLocal<>();

    /**
     * 清理in、out方法执行后的上下文信息，如清除重入标志
     */
    default void clear() {
        if (reentryMap.get() != null) {
            reentryMap.get().clear();
        }
    }

    default SoftReference<Object> getReentryMap(Integer i) {
        init();
        return reentryMap.get().get(i);
    }

    default Map<Integer, SoftReference<Object>> getReentryMap() {
        init();
        return reentryMap.get();
    }

    default void init() {
        if (null == reentryMap.get()) {
            reentryMap.set(new HashMap<Integer, SoftReference<Object>>());
        }
    }
}
