package pro.shushi.pamirs.boot.standard.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.spi.api.expression.ExpressionFunctionFetchApi;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;

/**
 * 标准表达式函数获取实现
 *
 * @author Adamancy Zhang at 20:25 on 2024-07-17
 */
@Order
@Component
@SPI.Service
public class StandardExpressionFunctionFetcher implements ExpressionFunctionFetchApi {

    @Override
    public List<FunctionDefinition> fetch() {
        return Models.origin().queryListByWrapper(Pops.<FunctionDefinition>lambdaQuery()
                .from(FunctionDefinition.MODEL_MODEL)
                .eq(FunctionDefinition::getNamespace, NamespaceConstants.expression));
    }
}
