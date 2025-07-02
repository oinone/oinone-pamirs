package pro.shushi.pamirs.framework.common.spi;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求会话初始化API
 *
 * @author Adamancy Zhang at 20:21 on 2025-04-01
 */
public interface SessionInitApi {

    void init(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam);
}
