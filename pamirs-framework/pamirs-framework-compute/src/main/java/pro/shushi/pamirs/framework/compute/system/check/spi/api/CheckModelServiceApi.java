package pro.shushi.pamirs.framework.compute.system.check.spi.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 模型校验SPI
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI
public interface CheckModelServiceApi {

    Boolean check(boolean returnWhenError, String model, Object data);

    Boolean check(boolean returnWhenError, ModelDefinition modelDefinition, Object data);

    Boolean check(boolean returnWhenError, ModelDefinition modelDefinition, Object data,
                  Function<String, FunctionDefinition> findCheckFunctionConsumer,
                  Map<String, Object> expressionContext);

    Boolean check(boolean returnWhenError, ModelDefinition modelDefinition, Object data,
                  Supplier<List<ComputeDefinition>> checksSupplier,
                  Function<String, FunctionDefinition> findCheckFunctionConsumer,
                  Map<String, Object> expressionContext,
                  Supplier<List<ExpressionDefinition>> expressionsSupplier);

}
