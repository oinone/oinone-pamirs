package pro.shushi.pamirs.meta.api.core.orm.systems.check;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.ValidationProcessor;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionScopeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 校验表达式处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@SPI.Service
public class BaseValidationProcessor implements ValidationProcessor {

    @Override
    public Map<String, ExpressionDefinition> fetchExpressionMap(Map<String, ExpressionDefinition> expressionDefinitionMap,
                                                                Validation validationAnnotation, String module,
                                                                String model, String sign) {
        Map<String, ExpressionDefinition> result = new HashMap<>();
        ComputeSceneEnum type = ComputeSceneEnum.VALIDATE;
        int i = 0;
        Validation.Rule[] ruleAnnotations = validationAnnotation.ruleWithTips();
        for (Validation.Rule rule : ruleAnnotations) {
            String expression = rule.value();
            result.put(ExpressionDefinition.sign(type, model, sign, expression),
                    fetchExpression(expressionDefinitionMap, rule.scope(), type, module, model, sign, expression,
                            rule.remark(), rule.tips(), rule.error(), rule.errorType(), rule.level(), i));
            i++;
        }
        String[] rules = validationAnnotation.rule();
        for (String rule : rules) {
            result.put(ExpressionDefinition.sign(type, model, sign, rule), fetchExpression(expressionDefinitionMap,
                    FunctionScopeEnum.BOTH, type, module, model, sign, rule,
                    null, null, null, null, null, i));
            i++;
        }
        return result;
    }

    @Override
    public ExpressionDefinition fetchExpression(Map<String, ExpressionDefinition> expressionDefinitionMap,
                                                FunctionScopeEnum scope, ComputeSceneEnum type,
                                                String module, String model, String location, String expression,
                                                String remark, String tips,
                                                String error, ErrorTypeEnum errorType, InformationLevelEnum level,
                                                Integer priority) {
        String expressionSign = ExpressionDefinition.sign(type, model, location, expression);
        ExpressionDefinition expressionDefinition = expressionDefinitionMap.get(expressionSign);
        if (null == expressionDefinition) {
            expressionDefinition = new ExpressionDefinition();
            expressionDefinition.setType(type)
                    .setModel(model).setLocation(location).setExpression(expression);
        }
        expressionDefinition.setScope(scope).setModule(module).setRemark(remark).setTips(tips)
                .setError(error).setErrorType(errorType).setLevel(level).setPriority(priority);
        return expressionDefinition;
    }

    @Override
    public Map<String, ComputeDefinition> fetchComputeFunctionMap(Map<String, ComputeDefinition> computeDefinitionMap,
                                                                  Validation validationAnnotation,
                                                                  String module, String model, String sign) {
        Map<String, ComputeDefinition> result = new HashMap<>();
        ComputeSceneEnum type = ComputeSceneEnum.VALIDATE;
        int i = 0;
        Validation.Fun[] checkAnnotations = validationAnnotation.checkWithTips();
        for (Validation.Fun fun : checkAnnotations) {
            String expression = fun.value();
            result.put(ExpressionDefinition.sign(type, model, sign, expression),
                    fetchComputeDefinition(computeDefinitionMap, fun.scope(), type, module, model, sign, expression,
                            fun.remark(), fun.tips(), i));
            i++;
        }
        String[] checks = validationAnnotation.check();
        for (String check : checks) {
            result.put(ExpressionDefinition.sign(type, model, sign, check),
                    fetchComputeDefinition(computeDefinitionMap, FunctionScopeEnum.BOTH, type, module, model, sign, check,
                            null, null, i));
            i++;
        }
        return result;
    }

    @Override
    public ComputeDefinition fetchComputeDefinition(Map<String, ComputeDefinition> computeDefinitionMap,
                                                    FunctionScopeEnum scope, ComputeSceneEnum type,
                                                    String module, String model, String location, String check,
                                                    String remark, String tips, Integer priority) {
        String checkSign = ComputeDefinition.sign(type, model, location, check);
        ComputeDefinition computeDefinition = computeDefinitionMap.get(checkSign);
        if (null == computeDefinition) {
            computeDefinition = new ComputeDefinition();
            computeDefinition.setType(type)
                    .setModel(model).setLocation(location).setFun(check);
        }
        computeDefinition.setScope(scope).setModule(module).setRemark(remark).setTips(tips).setPriority(priority);
        return computeDefinition;
    }

}
