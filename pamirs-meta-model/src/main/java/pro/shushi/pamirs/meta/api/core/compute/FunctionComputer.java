package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;

/**
 * 函数计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface FunctionComputer<R> {

    /**
     * 计算
     *
     * @param function
     * @param args
     * @return
     */
    R compute(Function function, Object... args);

    /**
     * 是否计算
     *
     * @param function
     * @return
     */
    boolean filter(Function function);

    /**
     * 函数类型
     *
     * @return
     */
    ScriptType type();

}
