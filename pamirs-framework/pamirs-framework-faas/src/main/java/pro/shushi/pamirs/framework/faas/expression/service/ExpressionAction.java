package pro.shushi.pamirs.framework.faas.expression.service;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.API;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;

/**
 * 表达式服务
 * <p>
 * 2021/3/6 2:49 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
@Fun(ExpressionDefinition.MODEL_MODEL)
public class ExpressionAction {

    @Resource
    private ExpressionService expressionComputer;

    @Function.Advanced(displayName = "校验API")
    @Function(openLevel = {LOCAL, API})
    public Boolean doValidate(ExpressionDefinition expression, Map<String, Object> context) {
        Result<Void> result = Exp.check(expression.getExpression());
        if (!result.isSuccess()) {
            PamirsSession.getMessageHub().error(result.getMessages());
            return false;
        }
        return expressionComputer.validate(expression, context);
    }

    @Function.Advanced(displayName = "执行API")
    @Function(openLevel = {LOCAL, API})
    public Object doExecute(ExpressionDefinition expression, Map<String, Object> context) {
        Result<Void> result = Exp.check(expression.getExpression());
        if (!result.isSuccess()) {
            PamirsSession.getMessageHub().error(result.getMessages());
            return null;
        }
        return expressionComputer.execute(expression, context);
    }

    @Function.Advanced(displayName = "批量校验API")
    @Function(openLevel = {LOCAL, API})
    public Boolean doValidateList(List<ExpressionDefinition> expressions, Map<String, Object> context) {
        if (!CollectionUtils.isEmpty(expressions)) {
            for (ExpressionDefinition rule : expressions) {
                Result<Void> result = Exp.check(rule.getExpression());
                if (!result.isSuccess()) {
                    PamirsSession.getMessageHub().error(result.getMessages());
                    return null;
                }
            }
        }
        return expressionComputer.validateList(expressions, context);
    }

    @Function.Advanced(displayName = "批量执行API")
    @Function(openLevel = {LOCAL, API})
    public List<Object> doExecuteList(List<ExpressionDefinition> expressions, Map<String, Object> context) {
        if (!CollectionUtils.isEmpty(expressions)) {
            for (ExpressionDefinition rule : expressions) {
                Result<Void> result = Exp.check(rule.getExpression());
                if (!result.isSuccess()) {
                    PamirsSession.getMessageHub().error(result.getMessages());
                    return null;
                }
            }
        }
        return expressionComputer.executeList(expressions, context);
    }

}
