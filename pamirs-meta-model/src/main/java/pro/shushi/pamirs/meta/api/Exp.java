package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.faas.ExpressionApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.List;
import java.util.Map;

/**
 * 表达式api快捷方式
 *
 * @author d
 * @version 2019-04-26
 */
@Slf4j
public class Exp {

    private static volatile ExpressionApi EXPRESSION_API;

    @SuppressWarnings("unused")
    public static String pre(String expressionDsl) {
        return getApi().pre(expressionDsl);
    }

    public static Result<Void> check(String expression) {
        return getApi().check(expression);
    }

    public static <T> T run(String expression) {
        return getApi().run(expression);
    }

    public static <T> T run(String expression, ScriptType type) {
        return getApi().run(expression, type);
    }

    public static <T> T run(String expression, Map<String, Object> context) {
        return getApi().run(expression, context);
    }

    public static <T> T run(String expression, ScriptType type, Map<String, Object> context) {
        return getApi().run(expression, type, context);
    }

    public static <T> T run(String expression, List<String> argNames, Object... args) {
        return getApi().run(expression, argNames, args);
    }

    public static <T> T run(String expression, ScriptType type, List<String> argNames, Object... args) {
        return getApi().run(expression, type, argNames, args);
    }

    public static <T> T fastRun(String expression, Map<String, Object> context) {
        return getApi().fastRun(expression, context);
    }

    public static <T> T fastRun(String expression, ScriptType type, Map<String, Object> context) {
        return getApi().fastRun(expression, type, context);
    }

    public static <T> T fastRun(String expression, List<String> argNames, Object... args) {
        return getApi().fastRun(expression, argNames, args);
    }

    public static <T> T fastRun(String expression, ScriptType type, List<String> argNames, Object... args) {
        return getApi().fastRun(expression, type, argNames, args);
    }

    private static ExpressionApi getApi() {
        if (null == EXPRESSION_API) {
            synchronized (Fun.class) {
                if (null == EXPRESSION_API) {
                    Exp.EXPRESSION_API = Spider.getDefaultExtension(ExpressionApi.class);
                }
            }
        }
        return EXPRESSION_API;
    }

}
