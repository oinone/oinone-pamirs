package pro.shushi.pamirs.boot.web.spi.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.ResourceModelQueryService;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * @author Gesi at 9:54 on 2025/9/16
 */
@Component
@Order
@SPI.Service
public class DefaultResourceModelQueryService implements ResourceModelQueryService {

    @Override
    public D queryResourceAddressByName(String sourceType, String countryName, String provinceName, String cityName, String districtName, String streetName) {
        return null;
    }

}
