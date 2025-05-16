package pro.shushi.pamirs.meta.api.session.cache;

import java.util.List;

/**
 * session list缓存 接口
 * <p>
 * 2021/8/19 1:00 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SessionListCache<T> {

    void clear();

    String type();

    List<T> get();

    void set(List<T> values);

    void addAll(List<T> values);

}
