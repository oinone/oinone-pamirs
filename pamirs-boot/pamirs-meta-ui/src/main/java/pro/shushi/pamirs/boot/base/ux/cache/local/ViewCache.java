package pro.shushi.pamirs.boot.base.ux.cache.local;

import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.cache.api.ViewCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.local.AbstractCacheWithDoubleKey;

import java.util.function.BiFunction;

/**
 * 视图缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ViewCache extends AbstractCacheWithDoubleKey<View> implements ViewCacheApi {

    @Override
    public String type() {
        return ViewCacheApi.class.getSimpleName();
    }

    @Override
    public BiFunction<String, String, String> keyGenerator() {
        return View::sign;
    }

}
