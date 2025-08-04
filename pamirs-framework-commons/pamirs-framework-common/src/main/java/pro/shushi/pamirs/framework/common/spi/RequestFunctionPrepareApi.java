package pro.shushi.pamirs.framework.common.spi;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 会话预处理API
 *
 * @author Adamancy Zhang on 2021-04-20 17:54
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RequestFunctionPrepareApi {

    /**
     * 预处理请求函数上下文
     *
     * @param request      {@link HttpServletRequest}
     * @param requestParam 请求参数
     */
    void prepare(HttpServletRequest request, PamirsRequestParam requestParam);
}
