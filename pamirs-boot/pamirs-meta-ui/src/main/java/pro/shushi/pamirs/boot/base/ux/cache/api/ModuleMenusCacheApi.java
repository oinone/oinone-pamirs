package pro.shushi.pamirs.boot.base.ux.cache.api;

import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.meta.api.session.cache.SessionCache;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;

import java.util.List;

/**
 * 模块菜单列表缓存接口
 *
 * <p>key为key:model
 * <p>
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModuleMenusCacheApi extends SessionCache<List<Menu>>, SessionCacheInitApi {

}
