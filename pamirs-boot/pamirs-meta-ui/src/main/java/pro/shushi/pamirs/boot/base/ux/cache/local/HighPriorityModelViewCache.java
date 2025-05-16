package pro.shushi.pamirs.boot.base.ux.cache.local;

import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.cache.api.HighPriorityModelViewCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ViewCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.local.AbstractCacheWithDoubleKey;

import java.util.function.BiFunction;

/**
 * 高优先级模型视图缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class HighPriorityModelViewCache extends AbstractCacheWithDoubleKey<View> implements HighPriorityModelViewCacheApi {

    @Override
    public String type() {
        return HighPriorityModelViewCacheApi.class.getSimpleName();
    }

    @Override
    public BiFunction<String, String, String> keyGenerator() {
        return View::modelViewType;
    }

}
