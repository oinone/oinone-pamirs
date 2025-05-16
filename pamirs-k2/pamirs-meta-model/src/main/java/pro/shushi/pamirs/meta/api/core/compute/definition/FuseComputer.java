package pro.shushi.pamirs.meta.api.core.compute.definition;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.Map;

/**
 * FuseModelComputer
 *
 * @author yakir on 2025/01/23 20:10.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FuseComputer extends CommonApi {

    Result<Void> compute(ComputeContext context, Meta meta, String model, ModelDefinition data, Map<String, Object> computeContext);
}
