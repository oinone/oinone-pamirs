package pro.shushi.pamirs.framework.compute.system.check.spi.service;

import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckFunctionServiceApi;
import pro.shushi.pamirs.framework.compute.system.check.util.CheckHelper;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.CheckProcessor;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 函数校验
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service
public class CheckFunctionService implements CheckFunctionServiceApi {

    @Override
    public Boolean check(boolean returnWhenError, FunctionDefinition functionDefinition,
                         Map<String, Object> requestArgs, Object[] args) {
        Map<String, Object> context = ExpressionDefinition.constructContext(functionDefinition.getNamespace(), null);
        if (null != requestArgs) {
            for (String argName : requestArgs.keySet()) {
                context.put(argName, requestArgs.get(argName));
            }
        }
        return check(returnWhenError, functionDefinition, args,
                (check) -> PamirsSession.getContext().findFunction(functionDefinition.getNamespace(), check)
                        .getFunctionDefinition(),
                context);
    }

    @Override
    public Boolean check(boolean returnWhenError, FunctionDefinition functionDefinition, Object data,
                         Function<String, FunctionDefinition> findFunctionConsumer,
                         Map<String, Object> expressionContext) {
        // 校验
        List<ComputeDefinition> checkList = CheckHelper.fetchFunctionChecker(functionDefinition.getNamespace(), functionDefinition.getFun(),
                sign -> PamirsSession.getContext().getComputeDefinitionList(ComputeSceneEnum.VALIDATE, FunctionDefinition.MODEL_MODEL, sign));
        List<ExpressionDefinition> ruleList = CheckHelper.fetchFunctionChecker(functionDefinition.getNamespace(), functionDefinition.getFun(),
                sign -> PamirsSession.getContext().getExpressionDefinitionList(ComputeSceneEnum.VALIDATE, FunctionDefinition.MODEL_MODEL, sign));
        if (CollectionUtils.isEmpty(checkList) && CollectionUtils.isEmpty(ruleList)) {
            return true;
        }
        return Spider.getDefaultExtension(CheckProcessor.class).check(returnWhenError,
                functionDefinition.getNamespace(), null,
                data, () -> checkList, findFunctionConsumer, expressionContext, () -> ruleList);
    }

}
