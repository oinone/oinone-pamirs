package pro.shushi.pamirs.framework.compute.system.check.spi.service;

import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate;
import pro.shushi.pamirs.framework.compute.system.check.util.CheckCommitter;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.CheckProcessor;
import pro.shushi.pamirs.meta.api.core.faas.ExpressionApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionScopeEnum;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 校验服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service
public class CheckService implements CheckProcessor {

    @Override
    public Boolean check(boolean returnWhenError, String model, String field, Object data,
                         Supplier<List<ComputeDefinition>> checksSupplier,
                         java.util.function.Function<String, FunctionDefinition> findCheckFunctionConsumer,
                         Map<String, Object> expressionContext,
                         Supplier<List<ExpressionDefinition>> expressionsSupplier
    ) {
        // 组装上下文
        Spider.getDefaultExtension(ExpressionApi.class).construct(data, model, field, expressionContext);

        boolean result = true;

        // 获取约束校验函数并校验
        List<ComputeDefinition> checks = checksSupplier.get();
        if (!CollectionUtils.isEmpty(checks)) {
            for (ComputeDefinition check : checks) {
                if (FunctionScopeEnum.CLIENT.equals(check.getScope())) {
                    continue;
                }
                String extraInfo = "model:" + model + ",field:" + field + ",check:" + check;
                FunctionDefinition checkFunctionDefinition = findCheckFunctionConsumer.apply(check.getFun());
                if (null == checkFunctionDefinition) {
                    throw PamirsException.construct(ComputeExpEnumerate.BASE_CHECK_FUNCTION_IS_NOT_EXIST_ERROR)
                            .appendMsg(extraInfo).errThrow();
                }
                Function checkFunction = Fun.generate(checkFunctionDefinition);
                result = CheckCommitter.commit(true, true, returnWhenError,
                        (obj) -> Models.directive().run(() -> Fun.run(checkFunction, obj)), extraInfo, data) && result;
                if (!result && returnWhenError) {
                    throw PamirsException.construct(FaasExpEnumerate.BASE_VALIDATION_FAIL_ERROR).errThrow();
                }
            }
        }

        // 获取约束校验表达式
        List<ExpressionDefinition> rules = expressionsSupplier.get();
        // 约束校验表达式校验
        if (!CollectionUtils.isEmpty(rules)) {
            result = (Boolean) Models.directive().run(() -> Fun.run(ExpressionDefinition.MODEL_MODEL, FunctionConstants.validateList, rules, expressionContext)) && result;
        }
        if (!result && returnWhenError) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_VALIDATION_FAIL_ERROR).errThrow();
        }
        return result;
    }

}
