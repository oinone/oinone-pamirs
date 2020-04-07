package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.ExpressionApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;

import java.util.List;
import java.util.Map;

/**
 * 表达式api快捷方式
 *
 * @author d
 * @version 2019-04-26
 */
@Slf4j
public class Exp<R> implements ExpressionApi<R> {

    public static <R> Exp<R> get() {
        return new Exp<>();
    }

    @Override
    public String pre(String expressionDsl) {
        return MetaApiFactory.getApi(ExpressionApi.class).pre(expressionDsl);
    }

    @Override
    public Result<String> check(String expression) {
        return (Result<String>) MetaApiFactory.getApi(ExpressionApi.class).check(expression);
    }

    @Override
    public R run(String expression) {
        return (R)MetaApiFactory.getApi(ExpressionApi.class).run(expression);
    }

    @Override
    public R run(String expression, Map<String, Object> context) {
        return (R)MetaApiFactory.getApi(ExpressionApi.class).run(expression, context);
    }

    @Override
    public R run(String expression, List<String> argNames, Object... args) {
        return (R)MetaApiFactory.getApi(ExpressionApi.class).run(expression, argNames, args);
    }

}
