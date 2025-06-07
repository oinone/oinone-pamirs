package pro.shushi.pamirs.core.common.cache;

/**
 * search cache
 *
 * @param <V> 缓存对象类型
 * @param <T> 值类型
 * @author Adamancy Zhang
 * @date 2020-11-25 11:08
 */
public interface SearchCache<K, V, T> extends MapCache<K, V> {

    /**
     * 重置搜索指针
     *
     * @return 重置结果
     */
    boolean reset();

    /**
     * 重置搜索指针并替换缓存对象
     *
     * @param target 缓存对象
     * @return 重置结果
     */
    boolean reset(T target);

    /**
     * 将搜索指针重置，并进行完全遍历，重新进行一次全缓存操作
     */
    void fill();
}
