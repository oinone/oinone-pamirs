package pro.shushi.pamirs.draft.session;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;

/**
 * 草稿会话
 *
 * @author Adamancy Zhang at 14:43 on 2025-10-20
 */
@Component
public class DraftSession implements SessionClearApi {

    private static final ThreadLocal<DraftSessionContext> storage = new ThreadLocal<>();

    public static DraftSessionContext get() {
        return storage.get();
    }

    public static void set(DraftSessionContext draft) {
        storage.set(draft);
    }

    @Override
    public void clear() {
        storage.remove();
    }
}
