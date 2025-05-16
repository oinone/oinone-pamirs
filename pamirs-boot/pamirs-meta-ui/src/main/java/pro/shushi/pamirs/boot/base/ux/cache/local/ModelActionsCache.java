package pro.shushi.pamirs.boot.base.ux.cache.local;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModelActionsCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.local.AbstractCache;

import java.util.List;

/**
 * 模型动作列表缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModelActionsCache extends AbstractCache<List<Action>> implements ModelActionsCacheApi {

    @Override
    public String type() {
        return ModelActionsCacheApi.class.getSimpleName();
    }

}
