package pro.shushi.pamirs.auth.api.runtime.session;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 分享授权会话
 *
 * @author Adamancy Zhang at 19:01 on 2024-04-19
 */
@Slf4j
@Component
@SPI.Service("AuthSharedAuthorizationSession")
public class AuthSharedAuthorizationSession implements SessionClearApi {

    private static final TransmittableThreadLocal<Storage> storage = new TransmittableThreadLocal<>();

    public static boolean isInit() {
        return storage.get() != null;
    }

    public static String getSharedCode() {
        Storage sharedSession = storage.get();
        if (sharedSession == null) {
            return null;
        }
        return sharedSession.getSharedCode();
    }

    public static String getAuthorizationCode() {
        Storage sharedSession = storage.get();
        if (sharedSession == null) {
            return null;
        }
        return sharedSession.getAuthorizationCode();
    }

    public static void setSession(String sharedCode, String authorizationCode) {
        storage.set(new Storage(sharedCode, authorizationCode));
    }

    public static void accessDenied() {
        storage.set(new Storage());
    }

    @Override
    public void clear() {
        storage.remove();
    }

    private static class Storage {

        private final String sharedCode;

        private final String authorizationCode;

        public Storage() {
            this.sharedCode = null;
            this.authorizationCode = null;
        }

        public Storage(String sharedCode, String authorizationCode) {
            this.sharedCode = sharedCode;
            this.authorizationCode = authorizationCode;
        }

        public String getSharedCode() {
            return sharedCode;
        }

        public String getAuthorizationCode() {
            return authorizationCode;
        }
    }
}
