package pro.shushi.pamirs.meta.api.core.faas;

import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 拦截计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface HookAfter {

    /**
     * 拦截器API
     *
     * @param function 执行函数
     * @param ret      方法出参
     * @return 返回值
     */
    Object run(Function function, Object ret);

}
