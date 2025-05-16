package pro.shushi.pamirs.framework.faas.spi.api.expression;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;

/**
 * 表达式函数获取API
 *
 * @author Adamancy Zhang at 20:06 on 2024-07-17
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExpressionFunctionFetchApi {

    /**
     * 获取所有可用表达式函数定义
     *
     * @return 表达式函数定义列表
     */
    List<FunctionDefinition> fetch();

}
