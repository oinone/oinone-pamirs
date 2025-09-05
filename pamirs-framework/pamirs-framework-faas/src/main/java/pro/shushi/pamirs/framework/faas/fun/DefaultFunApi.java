package pro.shushi.pamirs.framework.faas.fun;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.FunEngine;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.faas.FunApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Func;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.lambda.ref.*;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 函数API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SuppressWarnings("unchecked")
@Slf4j
@SPI.Service
@Component
public class DefaultFunApi implements FunApi {

    private static final FunEngine<?> funEngine = FunEngine.get();

    @Resource
    private FunctionManager functionManager;

    @Override
    public Function fetch(String namespace, String fun) {
        Function function = fetchAllowNull(namespace, fun);
        if (null == function) {
            log.error("{}, namespace:{},fun:{}", FaasExpEnumerate.BASE_FUNCTION_IS_NOT_EXIST_ERROR, namespace, fun);
            throw PamirsException.construct(FaasExpEnumerate.BASE_FUNCTION_IS_NOT_EXIST_ERROR).errThrow();
        }
        return function;
    }

    @Override
    public Function fetchAllowNull(String namespace, String fun) {
        Function function = Objects.requireNonNull(PamirsSession.getContext()).getFunctionAllowNull(namespace, fun);
        if (null == function) {
            function = PamirsSession.getContext().getFunctionAllowNull(BaseModel.MODEL_MODEL, fun);
        }
        if (null == function) {
            function = PamirsSession.getContext().getFunctionAllowNull(NamespaceConstants.pamirs, fun);
        }
        return function;
    }

    @Override
    public <T, P, R> Function fetch(Func<T, P, R> function) {
        Method method = LambdaUtil.fetchMethod(function);
        return fetch(method);
    }

    @Override
    public <T, R> Function fetch(Func0<T, R> function) {
        Method method = LambdaUtil.fetchMethod(function);
        return fetch(method);
    }

    @Override
    public <T, A1, A2, R> Function fetch(Func2<T, A1, A2, R> function) {
        Method method = LambdaUtil.fetchMethod(function);
        return fetch(method);
    }

    @Override
    public <T, A1, A2, A3, R> Function fetch(Func3<T, A1, A2, A3, R> function) {
        Method method = LambdaUtil.fetchMethod(function);
        return fetch(method);
    }

    @Override
    public <T, A1, A2, A3, A4, R> Function fetch(Func4<T, A1, A2, A3, A4, R> function) {
        Method method = LambdaUtil.fetchMethod(function);
        return fetch(method);
    }

    @Override
    public <T, A1, A2, A3, A4, A5, R> Function fetch(Func5<T, A1, A2, A3, A4, A5, R> function) {
        Method method = LambdaUtil.fetchMethod(function);
        return fetch(method);
    }

    @Override
    public <T, A1, A2, A3, A4, A5, A6, R> Function fetch(Func6<T, A1, A2, A3, A4, A5, A6, R> function) {
        Method method = LambdaUtil.fetchMethod(function);
        return fetch(method);
    }

    @Override
    public Function fetch(Method method) {
        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        return fetch(namespace, fun);
    }

    @Override
    public Function generate(FunctionDefinition functionDefinition) {
        if (null == functionDefinition) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_FUN_NOT_EXIST_ERROR).errThrow();
        }
        return new Function(functionDefinition);
    }

    @Override
    public Object run(String namespace, String fun, Object... args) {
        Function function = PamirsSession.getContext().getFunction(namespace, fun);
        return functionManager.runProxy(arguments -> funEngine.run(function, arguments), function, args);
    }

    @Override
    public Object run(Function function, Object... args) {
        return functionManager.runProxy(arguments -> funEngine.run(function, arguments), function, args);
    }

    @Override
    public <T, P, R> R run(Func<T, P, R> function, Object... args) {
        return (R) run(fetch(function), args);
    }

    @Override
    public <T, R> R run(Func0<T, R> function, Object... args) {
        return (R) run(fetch(function), args);
    }

    @Override
    public <T, A1, A2, R> R run(Func2<T, A1, A2, R> function, Object... args) {
        return (R) run(fetch(function), args);
    }

    @Override
    public <T, A1, A2, A3, R> R run(Func3<T, A1, A2, A3, R> function, Object... args) {
        return (R) run(fetch(function), args);
    }

    @Override
    public <T, A1, A2, A3, A4, R> R run(Func4<T, A1, A2, A3, A4, R> function, Object... args) {
        return (R) run(fetch(function), args);
    }

    @Override
    public <T, A1, A2, A3, A4, A5, R> R run(Func5<T, A1, A2, A3, A4, A5, R> function, Object... args) {
        return (R) run(fetch(function), args);
    }

    @Override
    public <T, A1, A2, A3, A4, A5, A6, R> R run(Func6<T, A1, A2, A3, A4, A5, A6, R> function, Object... args) {
        return (R) run(fetch(function), args);
    }

}
