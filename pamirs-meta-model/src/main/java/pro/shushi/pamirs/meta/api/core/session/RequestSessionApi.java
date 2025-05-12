package pro.shushi.pamirs.meta.api.core.session;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 请求session构造器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RequestSessionApi extends CommonApi {

    /**
     * 获取请求上下文
     *
     * @return RequestContext
     */
    RequestContext getContext();

    /**
     * 设置请求上下文
     *
     * @param context 请求上下文
     */
    void setContext(RequestContext context);

}
