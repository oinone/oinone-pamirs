package pro.shushi.pamirs.core.common.pipeline;

import java.util.Map;

/**
 * 交换对象
 *
 * @author Adamancy Zhang on 2021-04-26 14:17
 */
public interface PamirsExchange {

    /**
     * 获取交换体
     *
     * @return 交换体
     */
    Object getBody();

    /**
     * 设置新的交换体
     *
     * @param body 交换体
     */
    void setBody(Object body);

    /**
     * 获取所有属性
     *
     * @return 所有属性
     */
    Map<String, Object> getProperties();

    /**
     * 获取指定键值的属性值
     *
     * @param key 键值
     * @return 属性值
     */
    Object getProperty(String key);

    /**
     * 设置指定键值的属性值
     *
     * @param key   键值
     * @param value 属性值
     */
    void setProperty(String key, Object value);

    /**
     * 移除指定键值的属性值
     *
     * @param key 键值
     * @return 被移除的属性值
     */
    Object removeProperty(String key);

    /**
     * 获取异常
     *
     * @return 异常
     */
    Throwable getThrowable();

    /**
     * 设置异常
     *
     * @param throwable 异常
     */
    void setThrowable(Throwable throwable);

    /**
     * 中断判定
     *
     * @return 是否中断
     */
    boolean isInterrupted();

    /**
     * 执行中断
     */
    void interrupt();
}
