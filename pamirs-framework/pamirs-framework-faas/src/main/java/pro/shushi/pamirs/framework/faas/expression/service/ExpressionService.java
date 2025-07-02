package pro.shushi.pamirs.framework.faas.expression.service;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionScopeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_EXPRESSION_EXECUTE_ERROR;
import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_VALIDATE_EXPRESSION_ERROR;
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
public class ExpressionService {

    @Function.Advanced(displayName = "校验")
    @Function.fun(FunctionConstants.validate)
    @Function(openLevel = {LOCAL})
    public Boolean validate(ExpressionDefinition expression, Map<String, Object> context) {
        try {
            if (FunctionScopeEnum.CLIENT.equals(expression.getScope())) {
                return true;
            }
            checkExpressionActiveValue(context);
            return (Boolean) executeOne(context, expression, true);
        } finally {
            clearActiveValue(context);
        }
    }

    @Function.Advanced(displayName = "执行")
    @Function.fun(FunctionConstants.execute)
    @Function(openLevel = {LOCAL})
    public Object execute(ExpressionDefinition expression, Map<String, Object> context) {
        try {
            if (FunctionScopeEnum.CLIENT.equals(expression.getScope())) {
                return null;
            }
            checkExpressionActiveValue(context);
            return executeOne(context, expression, false);
        } finally {
            clearActiveValue(context);
        }
    }

    @Function.Advanced(displayName = "批量校验")
    @Function.fun(FunctionConstants.validateList)
    @Function(openLevel = {LOCAL})
    public Boolean validateList(List<ExpressionDefinition> expressions, Map<String, Object> context) {
        boolean result = true;
        try {
            if (!CollectionUtils.isEmpty(expressions)) {
                checkExpressionActiveValue(context);
                for (ExpressionDefinition rule : expressions) {
                    if (FunctionScopeEnum.CLIENT.equals(rule.getScope())) {
                        continue;
                    }
                    result = result && (Boolean) executeOne(context, rule, true);
                    if (!result && PamirsSession.getRequestVariables().returnWhenError()) {
                        break;
                    }
                }
            }
        } finally {
            clearActiveValue(context);
        }
        return result;
    }

    @Function.Advanced(displayName = "批量执行")
    @Function.fun(FunctionConstants.executeList)
    @Function(openLevel = {LOCAL})
    public List<Object> executeList(List<ExpressionDefinition> expressions, Map<String, Object> context) {
        try {
            if (!CollectionUtils.isEmpty(expressions)) {
                checkExpressionActiveValue(context);
                List<Object> resultList = new ArrayList<>();
                for (ExpressionDefinition rule : expressions) {
                    Object result = null;
                    if (!FunctionScopeEnum.CLIENT.equals(rule.getScope())) {
                        result = executeOne(context, rule, false);
                    }
                    resultList.add(result);
                }
                return resultList;
            }
        } finally {
            clearActiveValue(context);
        }
        return new ArrayList<>(0);
    }

    private Object executeOne(Map<String, Object> context, ExpressionDefinition rule, boolean isValidate) {
        Object result;
        try {
            result = Exp.run(rule.getExpression(), context);
            if (isValidate) {
                try {
                    boolean isMatch = (Boolean) result;
                    if (!isMatch) {
                        result = error(rule);
                    } else {
                        tips(rule);
                    }
                    return result;
                } catch (Exception e) {
                    throw PamirsException.construct(BASE_VALIDATE_EXPRESSION_ERROR, e)
                            .appendMsg(generateExtra(context, rule)).errThrow();
                }
            } else {
                tips(rule);
            }
        } catch (PamirsException pe) {
            throw pe;
        } catch (Exception e) {
            throw PamirsException.construct(BASE_EXPRESSION_EXECUTE_ERROR, e)
                    .appendMsg(generateExtra(context, rule)).errThrow();
        }
        return result;
    }

    private String generateExtra(Map<String, Object> context, ExpressionDefinition rule) {
        return "expression:" + JsonUtils.toJSONString(rule) + ",data:" + JsonUtils.toJSONString(context);
    }

    private boolean error(ExpressionDefinition rule) {
        return PamirsSession.getMessageHub().msg(() -> Message.init()
                .setCode(FwExpEnumerate.BASE_CHECK_DATA_ERROR.getCode() + "")
                .setMessage(rule.getError())
                .setErrorType(rule.getErrorType())
                .setLevel(null != rule.getLevel() ? rule.getLevel() : InformationLevelEnum.ERROR)
                .setData(rule.getExpression())).isSuccess();
    }

    private void tips(ExpressionDefinition rule) {
        PamirsSession.getMessageHub().msg(() -> Message.init()
                .setMessage(rule.getTips())
                .setErrorType(null)
                .setLevel(InformationLevelEnum.SUCCESS)
                .setData(rule.getExpression()));
    }

    private void checkExpressionActiveValue(Map<String, Object> context) {
        if (null == context) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_EXPRESSION_PARAM_IS_NULL_ERROR).errThrow();
        }
    }

    private void clearActiveValue(Map<String, Object> context) {
        context.clear();
    }

}
