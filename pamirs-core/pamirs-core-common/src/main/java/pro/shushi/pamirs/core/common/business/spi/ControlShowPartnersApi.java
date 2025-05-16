package pro.shushi.pamirs.core.common.business.spi;

import pro.shushi.pamirs.core.common.business.tmodel.CurrentPartner;
import pro.shushi.pamirs.core.common.business.tmodel.ShowPartners;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface ControlShowPartnersApi {

    ShowPartners queryAllPartners();

    ShowPartners changePartner(CurrentPartner currentPartner);
}
