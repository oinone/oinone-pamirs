package pro.shushi.pamirs.framework.connectors.cdn.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 文件上传主目录扩展，使用于如多租户场景
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2025/11/19
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CdnFileMainDirApi {

    /**
     * yaml中配置的CDN目标根据业务场景的扩充
     */
    String getExternalFileDir(String mainDir);

}
