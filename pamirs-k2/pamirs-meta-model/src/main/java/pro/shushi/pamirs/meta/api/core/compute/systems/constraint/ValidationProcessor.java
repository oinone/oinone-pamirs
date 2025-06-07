package pro.shushi.pamirs.meta.api.core.compute.systems.constraint;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionScopeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.Map;

/**
 * 校验表达式处理器
 * 2021/3/6 11:21 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI
public interface ValidationProcessor extends CommonApi {

    Map<String, ExpressionDefinition> fetchExpressionMap(Map<String, ExpressionDefinition> expressionDefinitionMap,
                                                         Validation validationAnnotation, String module,
                                                         String model, String sign);

    ExpressionDefinition fetchExpression(Map<String, ExpressionDefinition> expressionDefinitionMap,
                                         FunctionScopeEnum scope, ComputeSceneEnum type,
                                         String module, String model, String location, String expression,
                                         String remark, String tips,
                                         String error, ErrorTypeEnum errorType, InformationLevelEnum level,
                                         Integer priority);

    Map<String, ComputeDefinition> fetchComputeFunctionMap(Map<String, ComputeDefinition> computeDefinitionMap,
                                                           Validation validationAnnotation, String module,
                                                           String model, String sign);

    ComputeDefinition fetchComputeDefinition(Map<String, ComputeDefinition> computeDefinitionMap,
                                             FunctionScopeEnum scope, ComputeSceneEnum type,
                                             String module, String model, String location, String check,
                                             String remark, String tips, Integer priority);

}
