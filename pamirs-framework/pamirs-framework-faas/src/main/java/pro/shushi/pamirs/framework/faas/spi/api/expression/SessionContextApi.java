package pro.shushi.pamirs.framework.faas.spi.api.expression;

import pro.shushi.pamirs.meta.api.session.SessionContext;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * session上下文扩展点
 * 2021/3/3 10:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionContextApi {

    /**
     * 获取表达式的session上下文
     *
     * @return session上下文
     */
    SessionContext context(Map<String, Object> contextMap);

    HoldKeeper<SessionContextApi> holder = new HoldKeeper<>();

    static SessionContextApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(SessionContextApi.class));
    }
}
