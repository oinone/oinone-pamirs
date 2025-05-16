package pro.shushi.pamirs.auth.view.extend;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;

/**
 * 权限组扩展执行状态
 *
 * @author Adamancy Zhang at 10:30 on 2024-09-12
 */
@Component
public class AuthGroupExtendExecutionStatus implements SessionClearApi {

    private static final ThreadLocal<Boolean> status = new ThreadLocal<>();

    public void updates() {
        status.set(Boolean.TRUE);
    }

    public boolean isUpdates() {
        return Boolean.TRUE.equals(status.get());
    }

    @Override
    public void clear() {
        status.remove();
    }
}
