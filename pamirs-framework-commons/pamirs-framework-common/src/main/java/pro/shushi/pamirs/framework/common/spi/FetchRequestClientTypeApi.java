package pro.shushi.pamirs.framework.common.spi;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求客户端类型API
 *
 * @author Adamancy Zhang at 21:43 on 2024-11-18
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FetchRequestClientTypeApi {

    /**
     * 获取当前请求客户端类型
     *
     * @param request 当前请求
     * @return 客户端类型
     */
    ClientTypeEnum fetchCurrentClientType(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam);
}
