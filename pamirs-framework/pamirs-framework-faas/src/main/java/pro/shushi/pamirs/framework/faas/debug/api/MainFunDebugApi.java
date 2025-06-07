package pro.shushi.pamirs.framework.faas.debug.api;

import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

import java.util.Map;

/**
 * debug调试收集{主函数信息}记录API
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/9/4 1:51 下午
 */
public interface MainFunDebugApi {

    /**
     * 用于主函数调试，可以自定义返回所需要的调试信息
     *
     * @param function
     * @param computer
     * @param result
     * @param args
     * @return
     */
    Map<String, Object> debug(Function function, FunctionComputer computer, Object result, Object... args);

}
