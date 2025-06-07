package pro.shushi.pamirs.meta.api.core.faas;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.lambda.Func;
import pro.shushi.pamirs.meta.common.lambda.ref.*;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.lang.reflect.Method;

/**
 * 函数API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FunApi {

    /**
     * 根据函数编码从上下文获取调用对象
     * <p>
     * 若不存在，则抛出异常
     *
     * @param namespace 命名空间
     * @param fun       函数编码
     * @return 返回结果
     */
    Function fetch(String namespace, String fun);

    /**
     * 根据函数编码从上下文获取调用对象
     * <p>
     * 若不存在，则返回<code>null</code>
     *
     * @param namespace 命名空间
     * @param fun       函数编码
     * @return 返回结果
     */
    Function fetchAllowNull(String namespace, String fun);

    /**
     * 根据函数接口获取函数配置
     *
     * @param function 函数接口
     * @param <T>      函数所在类
     * @param <R>      函数返回值
     * @return 函数配置
     */
    <T, P, R> Function fetch(Func<T, P, R> function);

    <T, R> Function fetch(Func0<T, R> function);

    <T, A1, A2, R> Function fetch(Func2<T, A1, A2, R> function);

    <T, A1, A2, A3, R> Function fetch(Func3<T, A1, A2, A3, R> function);

    <T, A1, A2, A3, A4, R> Function fetch(Func4<T, A1, A2, A3, A4, R> function);

    <T, A1, A2, A3, A4, A5, R> Function fetch(Func5<T, A1, A2, A3, A4, A5, R> function);

    <T, A1, A2, A3, A4, A5, A6, R> Function fetch(Func6<T, A1, A2, A3, A4, A5, A6, R> function);

    /**
     * 根据方法获取函数
     *
     * @param method 方法
     * @return 函数
     */
    Function fetch(Method method);

    /**
     * 根据函数定义生成调用对象
     *
     * @param functionDefinition 函数定义
     * @return 函数配置
     */
    Function generate(FunctionDefinition functionDefinition);

    /**
     * 根据命名空间和函数编码执行函数，支持事务
     *
     * @param namespace 命名空间
     * @param fun       函数编码
     * @param args      入参
     * @return 返回值
     */
    <T> T run(String namespace, String fun, Object... args);

    /**
     * 执行函数，支持事务
     *
     * @param function 函数配置
     * @param args     入参
     * @return 返回值
     */
    <T> T run(Function function, Object... args);

    /**
     * 执行函数，支持事务
     *
     * @param function 函数配置
     * @param args     入参
     * @return 返回值
     */
    <T, P, R> R run(Func<T, P, R> function, Object... args);

    <T, R> R run(Func0<T, R> function, Object... args);

    <T, A1, A2, R> R run(Func2<T, A1, A2, R> function, Object... args);

    <T, A1, A2, A3, R> R run(Func3<T, A1, A2, A3, R> function, Object... args);

    <T, A1, A2, A3, A4, R> R run(Func4<T, A1, A2, A3, A4, R> function, Object... args);

    <T, A1, A2, A3, A4, A5, R> R run(Func5<T, A1, A2, A3, A4, A5, R> function, Object... args);

    <T, A1, A2, A3, A4, A5, A6, R> R run(Func6<T, A1, A2, A3, A4, A5, A6, R> function, Object... args);

}
