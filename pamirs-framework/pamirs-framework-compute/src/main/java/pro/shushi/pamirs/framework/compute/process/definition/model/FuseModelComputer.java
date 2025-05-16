package pro.shushi.pamirs.framework.compute.process.definition.model;

import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FuseComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.Map;

/**
 * FuseModelComputer
 *
 * @author yakir on 2025/01/23 15:06.
 */
@SPI.Service(ConstructComputer.SPI_NAME)
public class FuseModelComputer implements ModelComputer<Meta, ModelDefinition> {

    public static final String SPI_NAME = "fuseModel";

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, String model, ModelDefinition data, Map<String, Object> computeContext) {
        return Spider.getDefaultExtension(FuseComputer.class).compute(context, meta, model, data, computeContext);
    }
}
