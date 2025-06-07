package pro.shushi.pamirs.meta.api.core.session.watch;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * session变化监听
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface SessionWatcher<T> extends CommonApi {

    /**
     * 变更
     */
    void watch(T originValue, T newValue);

}
