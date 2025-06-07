package pro.shushi.pamirs.framework.faas.expression.api;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.framework.faas.fun.builtin.CollectionFunctions;
import pro.shushi.pamirs.framework.faas.guard.ScriptInvokeGuard;
import pro.shushi.pamirs.framework.faas.script.ScriptRunner;
import pro.shushi.pamirs.framework.faas.spi.api.expression.SessionContextApi;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.ExpressionApi;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.constant.ExpressionContextConstants;
import pro.shushi.pamirs.meta.util.ExpressionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表达式API默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SuppressWarnings("unused")
@SPI.Service
@Component
public class DefaultExpressionApi implements ExpressionApi, ExpressionContextConstants {

    @Override
    public String pre(String expression) {
        if (StringUtils.isBlank(expression)) {
            return expression;
        }
        // 先过滤字符串
        Map<String, String> replaceMap = new HashMap<>();
        expression = ExpressionUtils.ignoreString(expression, replaceMap);
        // 获取表达式中所有函数名
        List<String> funs = ExpressionUtils.fetchFunQuote(expression);
        Set<String> funSet = Sets.newHashSet(funs);
        for (String fun : funSet) {
            String methodSign = StringUtils.substringBeforeLast(fun, "(");
            Function function = PamirsSession.getContext().getFunctionAllowNull(NamespaceConstants.expression, methodSign);
            if (null == function) {
                throw PamirsException.construct(FaasExpEnumerate.BASE_EXPRESSION_NOT_EXIST_FUN_ERROR).appendMsg("函数名: " + methodSign).errThrow();
            }
            ScriptInvokeGuard.judgeAllow(function);
            expression = expression.replace(methodSign + "(", ExpressionUtils.fetchJavaPlaceholder(function));
        }
        // 替换逻辑运算符
        expression = ExpressionUtils.replaceLogical(expression);
        // 回填字符串
        for (String key : replaceMap.keySet()) {
            expression = expression.replace(key, replaceMap.get(key));
        }
        return expression;
    }

    @Override
    public Result<Void> check(String expression, ScriptType type) {
        Result<Void> result = new Result<>();
        if (StringUtils.isBlank(expression)) {
            result.error();
            return result;
        }
        try {
            ScriptRunner.parse(expression, type);
        } catch (PamirsException e) {
            result.error();
            result.addMessage(new Message().error(FaasExpEnumerate.BASE_SCRIPT_SYNTAX_ERROR)
                    .append(e.getMessage()).setData(expression));
        }
        return result;
    }

    @Override
    public <T> T run(String expression, ScriptType type) {
        return run(expression, type, null);
    }

    @Override
    public <T> T run(String expression, ScriptType type, Map<String, Object> context) {
        Function function = new Function()
                .setScriptType(type)
                .setCodes(expression)
                .setBitOptions(0L);
        return Models.directive().run(() -> Fun.run(function, context, true));
    }

    @Override
    public <T> T run(String expression, ScriptType type, List<String> argNames, Object... args) {
        return run(expression, type, generatorArgumentContext(argNames, args));
    }

    @Override
    public <T> T fastRun(String expression, ScriptType type, Map<String, Object> context) {
        Function function = new Function()
                .setScriptType(type)
                .setCodes(expression)
                .setBitOptions(0L);
        return Models.directive().run(() -> Fun.run(function, context, false));
    }

    @Override
    public <T> T fastRun(String expression, ScriptType type, List<String> argNames, Object... args) {
        return fastRun(expression, type, generatorArgumentContext(argNames, args));
    }

    @Override
    public Map<String, Object> construct(Object activeValue, String model, String field) {
        return construct(activeValue, model, field, null);
    }

    @Override
    public Map<String, Object> construct(Object activeValue, String model, String field, Map<String, Object> contextMap) {
        if (null == contextMap) {
            contextMap = new HashMap<>();
        }
        contextMap.put(ACTIVE_VALUE, activeValue);
        contextMap.put(ACTIVE_RECORD, activeValue);
        contextMap.put(ACTIVE_MODEL, model);
        if (null != field && !CONTEXT.equals(field)) {
            contextMap.put(ACTIVE_FIELD, field);
            contextMap.put(field, activeValue);
        }
        contextMap.put(CONTEXT, SessionContextApi.get().context(contextMap));
        return contextMap;
    }

    @SuppressWarnings("unchecked")
    private Object activeIds(Object activeValue) {
        if (activeValue instanceof List) {
            return CollectionFunctions.fetchListIds((List<Object>) activeValue);
        } else if (null != activeValue && activeValue.getClass().isArray()) {
            return CollectionFunctions.fetchListIds((Object[]) activeValue);
        }
        return null;
    }

    private Map<String, Object> generatorArgumentContext(List<String> argNames, Object... args) {
        if (CollectionUtils.isEmpty(argNames)) {
            return null;
        }
        if (argNames.size() != args.length) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_EXPRESSION_WRONG_ARGUMENT_LENGTH_ERROR).errThrow();
        }
        Map<String, Object> context = new HashMap<>();
        int i = 0;
        for (String argName : argNames) {
            context.put(argName, args[i]);
            i++;
        }
        if (!context.containsKey(CONTEXT)) {
            context.put(CONTEXT, SessionContextApi.get().context(context));
        }
        return context;
    }

}
