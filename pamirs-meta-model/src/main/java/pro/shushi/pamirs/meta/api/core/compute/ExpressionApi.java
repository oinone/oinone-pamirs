package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.dto.common.Result;

import java.util.List;
import java.util.Map;

/**
 * 表达式API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface ExpressionApi<R> {

    /**
     * 预处理表达式
     *
     * @param expression 表达式DSL
     * @return 表达式expression
     */
    String pre(String expression);

    /**
     * 检查表达式语法
     *
     * @param expression 表达式
     * @return
     */
    Result<String> check(String expression);

    /**
     * 执行表达式
     *
     * @param expression 表达式
     * @return
     */
    R run(String expression);

    /**
     * 根据上下文执行表达式
     *
     * @param expression 表达式
     * @param context 表达式上下文
     * @return
     */
    R run(String expression, Map<String, Object> context);

    /**
     * 根据入参执行表达式
     *
     * @param expression 表达式
     * @param argNames 参数名
     * @param args 参数值
     * @return
     */
    R run(String expression, List<String> argNames, Object... args);

}
