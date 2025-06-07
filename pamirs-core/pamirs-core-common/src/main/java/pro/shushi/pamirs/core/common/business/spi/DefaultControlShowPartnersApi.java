package pro.shushi.pamirs.core.common.business.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.business.tmodel.CurrentPartner;
import pro.shushi.pamirs.core.common.business.tmodel.ShowPartners;
import pro.shushi.pamirs.meta.common.spi.SPI;

@Order
@Component
@SPI.Service
public class DefaultControlShowPartnersApi implements ControlShowPartnersApi {

    @Override
    public ShowPartners queryAllPartners() {
        return new ShowPartners();
    }

    @Override
    public ShowPartners changePartner(CurrentPartner currentPartner) {
        return new ShowPartners();
    }
}
