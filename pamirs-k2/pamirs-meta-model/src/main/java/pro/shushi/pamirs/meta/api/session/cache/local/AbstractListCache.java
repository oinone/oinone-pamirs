package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionListCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表缓存抽象类
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public abstract class AbstractListCache<T> implements SessionListCache<T>, SessionCacheInitApi {

    private volatile List<T> list;

    @Override
    public void init() {
        list = new ArrayList<>();
    }

    @Override
    public void clear() {
        if (null != list) {
            list.clear();
            list = null;
        }
    }

    @Override
    public List<T> get() {
        return list;
    }

    @Override
    public void set(List<T> values) {
        list = values;
    }

    @Override
    public void addAll(List<T> values) {
        list.addAll(values);
    }

}
