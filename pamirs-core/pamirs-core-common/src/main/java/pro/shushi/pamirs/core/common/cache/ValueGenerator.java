package pro.shushi.pamirs.core.common.cache;

import java.util.function.Function;

/**
 * 值生成器
 *
 * @author Adamancy Zhang on 2021-04-27 09:30
 */
public interface ValueGenerator<K, V> extends Function<K, V> {

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    @Override
    V apply(K key);
}
