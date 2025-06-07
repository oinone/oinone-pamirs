package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.faas.ExtPointApi;
import pro.shushi.pamirs.meta.common.lambda.Func;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.lambda.ref.*;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.ExtNamespaceAndNameUtils;

import java.lang.reflect.Method;

/**
 * 扩展点api快捷方式
 *
 * @author d
 * @version 2019-04-26
 */
@Slf4j
public class Ext {

    private static volatile ExtPointApi EXT_POINT_API;

    public static <T> T run(String namespace, String extPointName, Object... args) {
        return getApi().run(namespace, extPointName, args);
    }

    public static <T, P, R> R run(Func<T, P, R> function, Object... args) {
        Method method = LambdaUtil.fetchMethod(function);
        return lambdaRun(method, args);
    }

    public static <T, R> R run(Func0<T, R> function, Object... args) {
        Method method = LambdaUtil.fetchMethod(function);
        return lambdaRun(method, args);
    }

    public static <T, A1, A2, R> R run(Func2<T, A1, A2, R> function, Object... args) {
        Method method = LambdaUtil.fetchMethod(function);
        return lambdaRun(method, args);
    }

    public static <T, A1, A2, A3, R> R run(Func3<T, A1, A2, A3, R> function, Object... args) {
        Method method = LambdaUtil.fetchMethod(function);
        return lambdaRun(method, args);
    }

    public static <T, A1, A2, A3, A4, R> R run(Func4<T, A1, A2, A3, A4, R> function, Object... args) {
        Method method = LambdaUtil.fetchMethod(function);
        return lambdaRun(method, args);
    }

    public static <T, A1, A2, A3, A4, A5, R> R run(Func5<T, A1, A2, A3, A4, A5, R> function, Object... args) {
        Method method = LambdaUtil.fetchMethod(function);
        return lambdaRun(method, args);
    }

    public static <T, A1, A2, A3, A4, A5, A6, R> R run(Func6<T, A1, A2, A3, A4, A5, A6, R> function, Object... args) {
        Method method = LambdaUtil.fetchMethod(function);
        return lambdaRun(method, args);
    }

    private static <R> R lambdaRun(Method method, Object... args) {
        String namespace = ExtNamespaceAndNameUtils.namespace(method);
        String name = ExtNamespaceAndNameUtils.name(method);
        return getApi().run(namespace, name, args);
    }

    private static ExtPointApi getApi() {
        if (null == EXT_POINT_API) {
            synchronized (Fun.class) {
                if (null == EXT_POINT_API) {
                    Ext.EXT_POINT_API = Spider.getDefaultExtension(ExtPointApi.class);
                }
            }
        }
        return EXT_POINT_API;
    }

}
