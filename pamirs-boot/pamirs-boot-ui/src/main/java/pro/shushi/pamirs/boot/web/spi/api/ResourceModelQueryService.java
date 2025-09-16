package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 资源模型查询api
 *
 * @author Gesi at 9:40 on 2025/9/16
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ResourceModelQueryService {

    D queryResourceAddressByName(String sourceType, String countryName, String provinceName, String cityName, String districtName, String streetName);

}
