package pro.shushi.pamirs.resource.api.spi.api;

import org.springframework.core.io.Resource;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface ResourceSystemInitializationIcon {

    void writeData(Resource resource);
}
