package pro.shushi.pamirs.meta.api.core.compute.systems.constraint;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 校验处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 6:17 下午
 */
@SPI
public interface CheckProcessor {

    Boolean check(boolean returnWhenError, String model, String field, Object data,
                  Supplier<List<ComputeDefinition>> checksSupplier,
                  Function<String, FunctionDefinition> findCheckFunctionConsumer,
                  Map<String, Object> expressionContext,
                  Supplier<List<ExpressionDefinition>> expressionsSupplier
    );

}
