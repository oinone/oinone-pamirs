package pro.shushi.pamirs.meta.api.session.cache.holder;

import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * SessionFillOwnSignApi持有者
 *
 * @author Adamancy Zhang at 20:26 on 2024-07-24
 */
public class SessionFillOwnSignApiHolder {

    private static final HoldKeeper<SessionFillOwnSignApi> holder = new HoldKeeper<>();

    public static SessionFillOwnSignApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(SessionFillOwnSignApi.class));
    }
}
