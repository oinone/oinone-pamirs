package pro.shushi.pamirs.resource.api.spi.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.resource.api.tmodel.ResourceIconUpload;

import java.util.List;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface ResourceIconUploadApi {

    List<ResourceIconUpload> uploadUrl(ResourceIconUpload Upload);
}
