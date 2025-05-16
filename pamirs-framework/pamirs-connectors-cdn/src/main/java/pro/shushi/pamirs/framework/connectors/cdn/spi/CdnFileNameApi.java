package pro.shushi.pamirs.framework.connectors.cdn.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 文件上传允许自定义文件名。
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/12
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CdnFileNameApi {

    /**
     * 根据原始的文件名，获取上传的到CDN的文件名。
     * @param fileName
     */
    String getNewFilename(String fileName);

}
