package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

/**
 * 函数API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface FunApi<R> {

    /**
     * 根据函数编码从上下文获取调用对象
     *
     * @param namespace
     * @param fun
     * @return
     */
    Function fetch(String namespace, String fun);

    /**
     * 根据函数定义生成调用对象
     *
     * @param functionDefinition
     * @return
     */
    Function generate(FunctionDefinition functionDefinition);

    /**
     * 根据命名空间和函数编码执行函数
     *
     * @param namespace
     * @param fun
     * @param args
     * @return
     */
    R run(String namespace, String fun, Object... args);

    /**
     * 执行函数
     *
     * @param function
     * @param args
     * @return
     */
    R run(Function function, Object... args);

    /**
     * 根据命名空间和函数编码执行函数，支持事务
     *
     * @param namespace
     * @param fun
     * @param args
     * @return
     */
    R runTx(String namespace, String fun, Object... args);

    /**
     * 根据命名空间和函数编码执行函数，支持事务
     *
     * @param function
     * @param args
     * @return
     */
    R runTx(Function function, Object... args);

    /**
     * 执行函数，支持事务
     *
     * @param function
     * @param txConfig
     * @param args
     * @return
     */
    R runTx(Function function, TxConfig txConfig, Object... args);

}
