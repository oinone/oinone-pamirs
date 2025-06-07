package pro.shushi.pamirs.boot.base.ux.cache.api;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheWithDoubleKey;

/**
 * 动作缓存接口
 * <p>
 * key为model.name
 * <p>
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ActionCacheApi extends SessionCacheWithDoubleKey<Action>, SessionCacheInitApi {

}
