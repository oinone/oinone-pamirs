package pro.shushi.pamirs.boot.web.session;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 当前会话的访问资源信息
 *
 * @author Adamancy Zhang at 17:20 on 2024-01-09
 */
@Component
public class AccessResourceInfoSession implements SessionClearApi {

    private static final TransmittableThreadLocal<Boolean> enabled = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<AccessResourceInfo> storage = new TransmittableThreadLocal<>();

    public static boolean isEnabled() {
        Boolean isEnabled = enabled.get();
        if (isEnabled == null) {
            isEnabled = Boolean.FALSE;
        }
        return isEnabled;
    }

    public static AccessResourceInfo getInfo() {
        return storage.get();
    }

    public static void setInfo(AccessResourceInfo info) {
        enabled.set(Boolean.TRUE);
        storage.set(info);
    }

    @Override
    public void clear() {
        enabled.remove();
        storage.remove();
    }
}
