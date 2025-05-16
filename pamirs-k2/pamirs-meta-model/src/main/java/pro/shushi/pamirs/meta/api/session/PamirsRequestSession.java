package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.api.core.session.RequestSessionApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * pamirs request session
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:45 下午
 */
public class PamirsRequestSession {

    private static final HoldKeeper<RequestSessionApi> holder = new HoldKeeper<>();

    public static RequestContext getContext() {
        RequestSessionApi sessionApi = getRequestSessionApi();
        return sessionApi.getContext();
    }

    public static void setContext(RequestContext context) {
        RequestSessionApi sessionApi = getRequestSessionApi();
        sessionApi.setContext(context);
    }

    public static RequestSessionApi getRequestSessionApi() {
        return holder.supply(() -> Spider.getDefaultExtension(RequestSessionApi.class));
    }

}
