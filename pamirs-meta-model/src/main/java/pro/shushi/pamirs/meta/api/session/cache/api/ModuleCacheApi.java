package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.session.cache.SessionCache;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Set;

/**
 * 模块缓存接口
 *
 * <p>key为module
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModuleCacheApi extends SessionCache<ModuleDefinition>, SessionCacheInitApi {

    ModuleDefinition getByName(String name);

    Set<String> keySet();

}
