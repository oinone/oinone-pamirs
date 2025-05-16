package pro.shushi.pamirs.meta.api.core.orm.systems.compute;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FuseComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.Map;

/**
 * DefaultFuseComputer
 *
 * @author yakir on 2025/01/23 20:16.
 */
@Order
@Component
@SPI.Service
public class DefaultFuseComputer implements FuseComputer {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, String model, ModelDefinition data, Map<String, Object> computeContext) {
        return new Result<>();
    }
}
