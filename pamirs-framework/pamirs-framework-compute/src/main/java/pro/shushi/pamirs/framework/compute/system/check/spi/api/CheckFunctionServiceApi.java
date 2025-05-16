package pro.shushi.pamirs.framework.compute.system.check.spi.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.Map;
import java.util.function.Function;

/**
 * 动作校验SPI
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI
public interface CheckFunctionServiceApi {

    Boolean check(boolean returnWhenError, FunctionDefinition functionDefinition, Map<String, Object> requestArgs, Object[] args);

    Boolean check(boolean returnWhenError, FunctionDefinition functionDefinition, Object data,
                  Function<String, FunctionDefinition> findFunctionConsumer,
                  Map<String, Object> expressionContext);

}
