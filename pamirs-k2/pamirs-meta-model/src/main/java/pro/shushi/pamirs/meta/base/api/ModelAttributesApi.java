package pro.shushi.pamirs.meta.base.api;

/**
 * 模型扩展属性api
 * <p>
 * 2022/5/7 1:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModelAttributesApi<T> {

    /**
     * 按属性名获取属性
     *
     * @param name 属性名
     * @return 属性值
     */
    Object getAttribute(String name);

    /**
     * 设置属性到对象
     *
     * @param name  属性名
     * @param value 属性值
     * @return 当前对象
     */
    T addAttribute(String name, Object value);

    /**
     * 移除属性
     *
     * @param name 属性名
     * @return 对象
     */
    T removeAttribute(String name);

}
