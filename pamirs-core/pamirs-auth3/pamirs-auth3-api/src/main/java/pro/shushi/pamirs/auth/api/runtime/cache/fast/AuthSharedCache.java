package pro.shushi.pamirs.auth.api.runtime.cache.fast;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分享权限二级缓存
 *
 * @author Adamancy Zhang at 17:21 on 2024-04-19
 */
@Order
@Component
@SPI.Service("AuthSharedCache")
public class AuthSharedCache implements SessionClearApi {

    private static final TransmittableThreadLocal<AuthSafeCache<String>> CACHE = new TransmittableThreadLocal<>();

    public static Set<String> getPaths(String authorizationCode) {
        return init().computeIfAbsent("getPaths:" + authorizationCode,
                () -> AuthSharedCache.getPaths0(authorizationCode));
    }

    private static Set<String> getPaths0(String authorizationCode) {
        return AuthApiHolder.getAuthSharedPageCacheService().get(authorizationCode);
    }

    public static Map<String, Object> getAllCache() {
        return Optional.ofNullable(CACHE.get())
                .map(AuthSafeCache::entrySet)
                .map(v -> v.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .map(Collections::unmodifiableMap)
                .orElse(Collections.emptyMap());
    }

    private static AuthSafeCache<String> init() {
        AuthSafeCache<String> data = CACHE.get();
        if (data == null) {
            data = new AuthSafeCache<>();
            CACHE.set(data);
        }
        return data;
    }

    @Override
    public void clear() {
        CACHE.remove();
    }
}
