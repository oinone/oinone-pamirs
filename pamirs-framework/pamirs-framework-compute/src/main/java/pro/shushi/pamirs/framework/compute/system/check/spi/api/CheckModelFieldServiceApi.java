package pro.shushi.pamirs.framework.compute.system.check.spi.api;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 字段校验SPI
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI
public interface CheckModelFieldServiceApi {

    Boolean check(boolean returnWhenError, ModelFieldConfig field, Object data);

    Boolean check(boolean returnWhenError, ModelFieldConfig field, Object data, String fun);

    Boolean check(boolean returnWhenError, ModelField modelField, Object data,
                  Function<String, FunctionDefinition> findCheckFunctionConsumer,
                  Map<String, Object> expressionContext);

    Boolean check(boolean returnWhenError, ModelField modelField, Object data,
                  Supplier<List<ComputeDefinition>> checksSupplier,
                  Function<String, FunctionDefinition> findCheckFunctionConsumer,
                  Map<String, Object> expressionContext,
                  Supplier<List<ExpressionDefinition>> expressionsSupplier);

}
