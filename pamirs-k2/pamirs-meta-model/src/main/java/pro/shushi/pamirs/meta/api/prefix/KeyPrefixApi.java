package pro.shushi.pamirs.meta.api.prefix;

import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 统一隔离key生成服务API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface KeyPrefixApi {

    @SuppressWarnings("unused")
    default String keyPrefix(Map<String, Object> context) {
        return CharacterConstants.SEPARATOR_EMPTY;
    }

}
