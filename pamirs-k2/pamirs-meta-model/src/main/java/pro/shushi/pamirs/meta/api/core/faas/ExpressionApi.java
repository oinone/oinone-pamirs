package pro.shushi.pamirs.meta.api.core.faas;

import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Map;

/**
 * 表达式API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExpressionApi {

    /**
     * 预处理表达式
     *
     * @param expression 表达式DSL
     * @return 表达式expression
     * @deprecated 5.1.0
     */
    @Deprecated
    String pre(String expression);

    /**
     * 检查表达式语法
     *
     * @param expression 表达式
     * @return 返回值
     */
    default Result<Void> check(String expression) {
        return check(expression, ScriptType.SCRIPT);
    }

    /**
     * 检查表达式语法
     *
     * @param expression 表达式
     * @param type       表达式类型（默认使用{@link ScriptType#SCRIPT}）
     * @return 返回值
     */
    Result<Void> check(String expression, ScriptType type);

    /**
     * 执行表达式
     *
     * @param expression 表达式
     * @return 返回值
     */
    default <T> T run(String expression) {
        return run(expression, ScriptType.SCRIPT);
    }

    /**
     * 执行表达式
     *
     * @param expression 表达式
     * @param type       表达式类型（默认使用{@link ScriptType#SCRIPT}）
     * @return 返回值
     */
    <T> T run(String expression, ScriptType type);

    /**
     * 根据上下文执行表达式
     *
     * @param expression 表达式
     * @param context    表达式上下文
     * @return 返回值
     */
    default <T> T run(String expression, Map<String, Object> context) {
        return run(expression, ScriptType.SCRIPT, context);
    }

    /**
     * 根据上下文执行表达式
     *
     * @param expression 表达式
     * @param type       表达式类型（默认使用{@link ScriptType#SCRIPT}）
     * @param context    表达式上下文
     * @return 返回值
     */
    <T> T run(String expression, ScriptType type, Map<String, Object> context);

    /**
     * 根据入参执行表达式
     *
     * @param expression 表达式
     * @param argNames   参数名
     * @param args       参数值
     * @return 返回值
     */
    default <T> T run(String expression, List<String> argNames, Object... args) {
        return run(expression, ScriptType.SCRIPT, argNames, args);
    }

    /**
     * 根据入参执行表达式
     *
     * @param expression 表达式
     * @param type       表达式类型（默认使用{@link ScriptType#SCRIPT}）
     * @param argNames   参数名
     * @param args       参数值
     * @return 返回值
     */
    <T> T run(String expression, ScriptType type, List<String> argNames, Object... args);

    /**
     * 根据上下文快速执行表达式（不处理参数空值）
     *
     * @param expression 表达式
     * @param context    表达式上下文
     * @return 返回值
     */
    default <T> T fastRun(String expression, Map<String, Object> context) {
        return fastRun(expression, ScriptType.SCRIPT, context);
    }

    /**
     * 根据上下文执行表达式
     *
     * @param expression 表达式
     * @param type       表达式类型（默认使用{@link ScriptType#SCRIPT}）
     * @param context    表达式上下文
     * @return 返回值
     */
    <T> T fastRun(String expression, ScriptType type, Map<String, Object> context);

    /**
     * 根据入参快速执行表达式（不处理参数空值）
     *
     * @param expression 表达式
     * @param argNames   参数名
     * @param args       参数值
     * @return 返回值
     */
    default <T> T fastRun(String expression, List<String> argNames, Object... args) {
        return fastRun(expression, ScriptType.SCRIPT, argNames, args);
    }

    /**
     * 根据入参快速执行表达式（不处理参数空值）
     *
     * @param expression 表达式
     * @param type       表达式类型（默认使用{@link ScriptType#SCRIPT}）
     * @param argNames   参数名
     * @param args       参数值
     * @return 返回值
     */
    <T> T fastRun(String expression, ScriptType type, List<String> argNames, Object... args);

    /**
     * 构造表达式上下文
     *
     * @param activeValue 当前选中值
     * @param model       当前模型
     * @param field       当前字段
     * @return 上下文
     */
    Map<String, Object> construct(Object activeValue, String model, String field);

    /**
     * 构造表达式上下文
     *
     * @param activeValue 当前选中值
     * @param model       当前模型
     * @param field       当前字段
     * @param contextMap  上下文
     * @return 上下文
     */
    Map<String, Object> construct(Object activeValue, String model, String field, Map<String, Object> contextMap);

}
