package pro.shushi.pamirs.core.common.cache;

/**
 * 唯一键生成器
 *
 * @author Adamancy Zhang on 2021-04-27 09:26
 */
@FunctionalInterface
public interface UniqueKeyGenerator<V, K> {

    /**
     * 生成唯一键
     *
     * @param value 值
     * @return 唯一键
     */
    K generator(V value);
}
