package pro.shushi.pamirs.core.common.map;

import jakarta.validation.constraints.NotNull;

/**
 * 双向实体映射Map
 *
 * @author Adamancy Zhang at 22:52 on 2021-08-27
 */
public interface EntityMap<K, V> {

    /**
     * 添加键值
     *
     * @param key   键
     * @param value 值
     */
    void put(@NotNull K key, @NotNull V value);

    /**
     * 通过键获取值
     *
     * @param key 键
     * @return 值
     */
    V getValueByKey(@NotNull K key);

    /**
     * 通过值获取键
     *
     * @param value 值
     * @return 键
     */
    K getKeyByValue(@NotNull V value);
}
