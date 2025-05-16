package pro.shushi.pamirs.boot.web.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import pro.shushi.pamirs.boot.base.model.LayoutDefinition;
import pro.shushi.pamirs.meta.api.cache.CacheProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LayoutDefinitionCache {

    private static final Cache<String/**name*/, LayoutDefinition> layoutDefCache = CacheProxy.getInstance(
            Caffeine.newBuilder().maximumSize(50).expireAfterWrite(60 * 60, TimeUnit.SECONDS).build()
    );

    // LayoutDefinition按Name唯一. 缓存里面不能缓存已经编译好的LayoutDefinition，因为同一个layout会对应N个页面(编译时需要动态设置)
    public static LayoutDefinition queryLayoutByName(String name) {
        return layoutDefCache.get(name, k -> {
            return new LayoutDefinition().setName(name).queryOne();
        });
    }

    public static void clearLayoutDefByName(String name) {
        layoutDefCache.invalidate(name);
    }

    public static List<LayoutDefinition> queryLayoutsByName(List<String> layoutNames) {
        List<LayoutDefinition> layouts = new ArrayList<>();
        for (String layoutName:layoutNames) {
            LayoutDefinition layoutDef = queryLayoutByName(layoutName);
            if (layoutDef != null) {
                layouts.add(layoutDef);
            }
        }
        return layouts;
    }
}
