package pro.shushi.pamirs.meta.api.core.faas;

import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 拦截计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface HookBefore {

    /**
     * 拦截器API
     *
     * @param function 执行函数
     * @param args     方法入参
     * @return 返回值
     */
    Object run(Function function, Object... args);

}
