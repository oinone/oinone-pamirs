package pro.shushi.pamirs.user.api.login;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.spi.PamirsUserInfoCacheApi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserInfoCache implements SessionClearApi {

    private static final TransmittableThreadLocal<Map<Long, PamirsUser>> cache = new TransmittableThreadLocal<>();

    private static final HoldKeeper<PamirsUserInfoCacheApi> userInfoCacheApiHolder = new HoldKeeper<>();

    public static Map<Long, PamirsUser> cacheInit() {
        Map<Long, PamirsUser> userMap = cache.get();
        if (userMap == null) {
            userMap = new ConcurrentHashMap<>();
            cache.set(userMap);
        }
        return userMap;
    }

    public static Map<Long, PamirsUser> get() {
        Map<Long, PamirsUser> userMap = cache.get();
        if (userMap == null) {
            userMap = new HashMap<>();
        }
        return userMap;
    }

    private static PamirsUserInfoCacheApi getCacheApi() {
        return userInfoCacheApiHolder.supply(() -> Spider.getDefaultExtension(PamirsUserInfoCacheApi.class));
    }

    public static void init() {
        getCacheApi().init();
    }

    public static PamirsUser queryUserById(Long userId) {
        return getCacheApi().queryUserById(userId);
    }

    public static void clearUserById(Long userId) {
        getCacheApi().clearUserById(userId);
    }

    public static void putUserInfo(PamirsUser user) {
        getCacheApi().putUserInfo(user);
    }

    @Override
    public void clear() {
        cache.remove();
    }
}
