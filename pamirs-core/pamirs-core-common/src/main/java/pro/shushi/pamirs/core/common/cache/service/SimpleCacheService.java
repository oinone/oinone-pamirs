package pro.shushi.pamirs.core.common.cache.service;

import jakarta.annotation.Nonnull;

import java.util.function.Supplier;

/**
 * 简单缓存服务
 *
 * @author Adamancy Zhang on 2021-06-08 18:06
 */
public interface SimpleCacheService<T> {

    /**
     * 空值标记
     */
    String EMPTY_FLAG = "EMPTY";

    /**
     * 拉取缓存内容
     *
     * @param key 键值
     * @return 缓存内容
     */
    T pull(String key);

    /**
     * 拉取缓存内容，若缓存值不存在，则通过提供者进行获取，并推送至缓存
     *
     * @param key      键值
     * @param supplier 提供者
     * @return 缓存内容
     */
    T pull(String key, @Nonnull Supplier<T> supplier);

    /**
     * 推送新的缓存数据
     *
     * @param key  键值
     * @param data 新的缓存数据
     * @return 缓存内容
     */
    T push(String key, T data);

    /**
     * 清空缓存
     *
     * @param key 键值
     */
    void clear(String key);
}
