package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.faas.FunApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.lambda.Func;
import pro.shushi.pamirs.meta.common.lambda.ref.*;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

/**
 * 函数api快捷方式
 *
 * @author d
 * @version 2019-04-26
 */
@Slf4j
public class Fun {

    private static volatile FunApi FUN_API;

    public static Function fetch(String namespace, String fun) {
        return getApi().fetch(namespace, fun);
    }

    public static Function generate(FunctionDefinition functionDefinition) {
        return getApi().generate(functionDefinition);
    }

    public static <T> T run(String namespace, String fun, Object... args) {
        return getApi().run(namespace, fun, args);
    }

    public static <T> T run(Function function, Object... args) {
        return getApi().run(function, args);
    }

    public static <T, P, R> R run(Func<T, P, R> function, Object... args) {
        return getApi().run(function, args);
    }

    public static <T, R> R run(Func0<T, R> function, Object... args) {
        return getApi().run(function, args);
    }

    public static <T, A1, A2, R> R run(Func2<T, A1, A2, R> function, Object... args) {
        return getApi().run(function, args);
    }

    public static <T, A1, A2, A3, R> R run(Func3<T, A1, A2, A3, R> function, Object... args) {
        return getApi().run(function, args);
    }

    public static <T, A1, A2, A3, A4, R> R run(Func4<T, A1, A2, A3, A4, R> function, Object... args) {
        return getApi().run(function, args);
    }

    public static <T, A1, A2, A3, A4, A5, R> R run(Func5<T, A1, A2, A3, A4, A5, R> function, Object... args) {
        return getApi().run(function, args);
    }

    public static <T, A1, A2, A3, A4, A5, A6, R> R run(Func6<T, A1, A2, A3, A4, A5, A6, R> function, Object... args) {
        return getApi().run(function, args);
    }

    private static FunApi getApi() {
        if (null == FUN_API) {
            synchronized (Fun.class) {
                if (null == FUN_API) {
                    Fun.FUN_API = Spider.getDefaultExtension(FunApi.class);
                }
            }
        }
        return FUN_API;
    }

}
