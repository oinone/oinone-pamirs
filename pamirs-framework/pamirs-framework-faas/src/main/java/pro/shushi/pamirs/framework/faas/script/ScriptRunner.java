package pro.shushi.pamirs.framework.faas.script;

import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.framework.faas.script.engine.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.MapUtils;
import pro.shushi.pamirs.meta.util.ExpressionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 脚本执行器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
public class ScriptRunner {

    private static final Map<String, PamirsScriptEngine> scriptEngineMap = new ConcurrentHashMap<>();

    static {
        PamirsScriptEngine defaultScriptEngine = new GroovyScriptEngine();
        scriptEngineMap.put(ScriptType.JS.getType(), new JsScriptEngine());
        scriptEngineMap.put(ScriptType.EL.getType(), new MvelScriptEngine());
        scriptEngineMap.put(ScriptType.GROOVY.getType(), defaultScriptEngine);
        scriptEngineMap.put(ScriptType.SCRIPT.getType(), defaultScriptEngine);
    }

    private static PamirsScriptEngine getEngine(String type) {
        return scriptEngineMap.computeIfAbsent(type, CommonScriptEngine::new);
    }

    public static <R> R run(Function function, Object... args) {
        ScriptType scriptType = function.getScriptType();
        if (scriptType == null) {
            scriptType = ScriptType.SCRIPT;
        }
        String expression = function.getCodes();
        try {
            return run(expression, scriptType.getType(), makeContext(function, args));
        } catch (Exception e) {
            log.error("callModel:{},exp:{}", JsonUtils.toJSONString(function), expression);
            throw e;
        }
    }

    public static <R> R run(String script) {
        return run(script, ScriptType.SCRIPT.getType(), null);
    }

    @SuppressWarnings("unchecked")
    public static <R> R run(String expression, String type, Map<String, Object> context) {
        try {
            PamirsScriptEngine scriptEngine = getEngine(type);
            if (context == null) {
                return (R) scriptEngine.eval(expression);
            }
            return (R) scriptEngine.eval(expression, context);
        } catch (ScriptException e) {
            Throwable cause = e.getCause();
            if (cause instanceof PamirsException) {
                throw (PamirsException) cause;
            }
            throw PamirsException.construct(FaasExpEnumerate.BASE_SCRIPT_INVOKE_ERROR, e).appendMsg(MessageFormat.format("script: {0}", expression)).errThrow();
        }
    }

    @Deprecated
    public static <R> R run(String script, Bindings bindings) {
        return run(script, ScriptType.SCRIPT.getType(), bindings);
    }

    /**
     * 通过Exp#run方式调用必然会传入两个参数，不考虑其他入参情况
     * <p>
     * 参数1: Map 表达式上下文<br>
     * 参数2: Boolean 是否处理上下文参数
     * </p>
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static Map<String, Object> makeContext(Function function, Object... args) {
        String expression = function.getCodes();
        Map<String, Object> context = null;
        boolean isParameterProcess = true;
        if (args.length == 2) {
            context = (Map<String, Object>) (args[0]);
            isParameterProcess = (boolean) args[1];
        } else if (args.length == 1) {
            context = (Map<String, Object>) (args[0]);
        }
        if (null == context) {
            context = new HashMap<>();
        } else if (isParameterProcess) {
            MapUtils.removeNullValue(context);
            List<String> variables = ExpressionUtils.fetchStrictVariable(expression);
            for (String variable : variables) {
                context.putIfAbsent(variable, null);
            }
        }
        return context;
    }

    @Deprecated
    public static Object[] makeArgs(String expression, Object[] arg) {
        if (null == arg || arg.length == 0) {
            return new Object[]{};
        }
        //noinspection rawtypes,unchecked
        Map<String, Object> context = (Map) arg[0];
        if (null == context) {
            return new Object[]{};
        }
        List<String> variables = ExpressionUtils.fetchOriginStrictVariable(expression);
        Object[] args = new Object[variables.size()];
        int i = 0;
        for (String variable : variables) {
            args[i] = context.get(variable);
            i++;
        }
        return args;
    }

    @Deprecated
    public static CompiledScript parse(String scriptString) {
        return parse(scriptString, ScriptType.SCRIPT);
    }

    @Deprecated
    public static CompiledScript parse(String scriptString, ScriptType type) {
        PamirsScriptEngine scriptEngine = scriptEngineMap.get(type.getType());
        log.debug((String) scriptEngine.getFactory().getParameter(ScriptEngine.LANGUAGE));
        log.debug((String) scriptEngine.getFactory().getParameter("THREADING"));
        try {
            return scriptEngine.compile(scriptString);
        } catch (ScriptException e) {
            throw PamirsException.construct(FaasExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
    }

}
