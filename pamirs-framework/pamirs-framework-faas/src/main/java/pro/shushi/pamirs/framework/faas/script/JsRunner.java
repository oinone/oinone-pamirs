package pro.shushi.pamirs.framework.faas.script;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.FunctionUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * js执行器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
public class JsRunner {

    public static Object run(Function function, Object... args) {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("javascript");
        try {
            String[] argNames = FunctionUtils.fetchArgNames(function.getArguments());
            String methodSignString = "run(" + StringUtils.join(argNames, ",") + ")";
            se.eval("function " + methodSignString + "{" + function.getCodes() + "}");
            Invocable inv2 = (Invocable) se;
            return inv2.invokeFunction("run", args);
        } catch (Exception e) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_JAVASCRIPT_RUN_ERROR, e).errThrow();
        }
    }

    public static Object run(String script) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try {
            return engine.eval(script);
        } catch (ScriptException e) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_JAVASCRIPT_EVAL_ERROR, e).errThrow();
        }
    }

}
