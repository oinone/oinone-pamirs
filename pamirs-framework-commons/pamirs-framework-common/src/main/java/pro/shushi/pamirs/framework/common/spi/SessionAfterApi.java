package pro.shushi.pamirs.framework.common.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 会话预处理API
 *
 * @author cpc on 2024-01-13
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionAfterApi {

    void after(HttpServletRequest request, HttpServletResponse response);
}
