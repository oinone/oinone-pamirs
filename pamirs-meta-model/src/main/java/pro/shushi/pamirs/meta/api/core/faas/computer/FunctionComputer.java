package pro.shushi.pamirs.meta.api.core.faas.computer;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;

/**
 * 函数计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface FunctionComputer {

    /**
     * 计算
     *
     * @param function 函数配置
     * @param args     入参
     * @return 计算结果
     */
    Object compute(Function function, Object... args);

    /**
     * 是否计算
     *
     * @param filterContext 上下文
     * @param function      函数配置
     * @return 是否计算
     */
    boolean filter(FilterContext filterContext, Function function);

    /**
     * 函数类型
     *
     * @return 函数类型
     */
    ScriptType type();

}
