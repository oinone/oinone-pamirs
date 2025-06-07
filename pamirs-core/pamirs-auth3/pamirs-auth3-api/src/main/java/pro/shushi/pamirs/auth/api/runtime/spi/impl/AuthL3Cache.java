package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthSafeCache;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 权限三级缓存
 *
 * @author Adamancy Zhang at 18:11 on 2024-03-01
 */
@Order
@Component
@SPI.Service("AuthL3Cache")
class AuthL3Cache implements SessionClearApi {

    private static final TransmittableThreadLocal<AuthSafeCache<String>> CACHE = new TransmittableThreadLocal<>();

    static AuthSafeCache<String> init() {
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
