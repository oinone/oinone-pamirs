package pro.shushi.pamirs.core.common.cache.service.template;

import jakarta.annotation.Nonnull;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.cache.service.SimpleCacheService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.function.Supplier;

import static pro.shushi.pamirs.core.common.FetchUtil.cast;

/**
 * 抽象缓存服务模板
 *
 * @author Adamancy Zhang at 16:35 on 2021-06-20
 */
@Slf4j
public abstract class AbstractCacheServiceTemplate<T, D> implements SimpleCacheService<T> {

    /**
     * 序列化
     *
     * @param key  键
     * @param data 原始数据
     * @return 序列化后的数据
     */
    protected abstract D serializable(String key, T data);

    /**
     * 反序列化
     *
     * @param key  键
     * @param data 序列化后的数据
     * @return 原始数据
     */
    protected abstract T deserialization(String key, D data);

    /**
     * 获取缓存数据
     *
     * @param key 键
     * @return 序列化后的数据
     */
    protected abstract Object getCacheData(String key);

    /**
     * 设置缓存数据
     *
     * @param key  键
     * @param data 序列化后的数据
     */
    protected abstract void setCacheData(String key, D data);

    /**
     * 设置空对象
     *
     * @param key 键
     */
    protected abstract void setEmptyObject(String key);

    /**
     * 判断是否为空对象
     *
     * @param key  键
     * @param data 序列化后的数据
     * @return 是否为空对象
     */
    protected abstract boolean isEmptyObject(String key, Object data);

    /**
     * 缓存键预处理
     *
     * @return 处理后的缓存键
     */
    protected String prepareKey(String key) {
        return key;
    }

    /**
     * 最小化存储
     *
     * @param data 原始对象
     * @return 最小化存储对象
     */
    protected T minimizeStorage(T data) {
        return data;
    }

    @Override
    public T pull(String key) {
        return pull(key, () -> null);
    }

    @Override
    public T pull(String key, @Nonnull Supplier<T> supplier) {
        String finalKey = prepareKey(key);
        Object serializableData = getCacheData(finalKey);
        if (ObjectHelper.isBlank(serializableData)) {
            T data = supplier.get();
            if (data == null) {
                setEmptyObject(finalKey);
            } else {
                data = push(key, data);
            }
            return data;
        } else if (isEmptyObject(finalKey, serializableData)) {
            return null;
        } else {
            try {
                return deserialization(finalKey, cast(serializableData));
            } catch (Throwable e) {
                log.error("cache error.", e);
                setEmptyObject(finalKey);
                return null;
            }
        }
    }

    @Override
    public T push(String key, T data) {
        key = prepareKey(key);
        data = minimizeStorage(data);
        setCacheData(key, serializable(key, data));
        return data;
    }
}
