package pro.shushi.pamirs.framework.connectors.cdn.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 文件上传主目录扩展，使用于如多租户场景
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2025/11/19
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultCdnFileMainDirApi implements CdnFileMainDirApi {

    @Override
    public String getExternalFileDir(String mainDir) {
        return mainDir;
    }
}
