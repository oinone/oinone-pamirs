package pro.shushi.pamirs.boot.base.ux.cache.local;

import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModuleMenusCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.local.AbstractCache;

import java.util.List;

/**
 * 模块菜单列表缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModuleMenusCache extends AbstractCache<List<Menu>> implements ModuleMenusCacheApi {

    @Override
    public String type() {
        return ModuleMenusCacheApi.class.getSimpleName();
    }

}
