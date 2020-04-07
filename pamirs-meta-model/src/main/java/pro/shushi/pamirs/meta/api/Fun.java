package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.FunApi;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

/**
 * 函数api快捷方式
 *
 * @author d
 * @version 2019-04-26
 */
@Slf4j
public class Fun<R> implements FunApi<R> {

    public static <R> Fun<R> get() {
        return new Fun<>();
    }

    @Override
    public Function fetch(String namespace, String fun) {
        return MetaApiFactory.getApi(FunApi.class).fetch(namespace, fun);
    }

    @Override
    public Function generate(FunctionDefinition functionDefinition) {
        return MetaApiFactory.getApi(FunApi.class).generate(functionDefinition);
    }

    @Override
    public R run(String namespace, String fun, Object... args) {
        return (R)MetaApiFactory.getApi(FunApi.class).run(namespace, fun, args);
    }

    @Override
    public R run(Function function, Object... args) {
        return (R)MetaApiFactory.getApi(FunApi.class).run(function, args);
    }

    @Override
    public R runTx(String namespace, String fun, Object... args) {
        return (R)MetaApiFactory.getApi(FunApi.class).runTx(namespace, fun, args);
    }

    @Override
    public R runTx(Function function, Object... args) {
        return (R)MetaApiFactory.getApi(FunApi.class).runTx(function, args);
    }

    @Override
    public R runTx(Function function, TxConfig txConfig, Object... args) {
        return (R)MetaApiFactory.getApi(FunApi.class).runTx(function, txConfig, args);
    }

}
