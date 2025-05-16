package pro.shushi.pamirs.meta.api.core.faas.hook;

/**
 * 拦截器API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface HookApi {

    /**
     * 前置拦截器
     *
     * @param namespace 执行函数命名空间
     * @param fun       执行函数编码
     * @param args      执行参数
     */
    void before(String namespace, String fun, Object... args);

    /**
     * 后置拦截器
     *
     * @param namespace 执行函数命名空间
     * @param fun       执行函数编码
     * @param ret       返回值
     */
    void after(String namespace, String fun, Object ret);

}
