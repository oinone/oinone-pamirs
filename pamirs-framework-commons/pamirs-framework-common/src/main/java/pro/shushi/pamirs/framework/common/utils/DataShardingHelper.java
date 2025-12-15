package pro.shushi.pamirs.framework.common.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 数据分片帮助类
 *
 * @author Adamancy Zhang at 15:36 on 2023-12-02
 */
public class DataShardingHelper {

    /**
     * mysql默认eq_range_index_dive_limit参数
     */
    public static final int DEFAULT_EACH_SHARD_MAX = 200;

    /**
     * 默认列表提供者
     */
    private static final Function<List<?>, List<?>> DEFAULT_LIST_SUPPLIER = (list) -> {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    };

    /**
     * 默认分片列表提供者
     */
    private static final Function<List<?>, List<?>> DEFAULT_SHARD_LIST_SUPPLIER = (list) -> {
        if (list == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(list);
    };

    /**
     * 默认列表提供者
     */
    private static final Function<Map<?, ?>, Map<?, ?>> DEFAULT_MAP_SUPPLIER = (map) -> {
        if (map == null) {
            return new HashMap<>();
        }
        return map;
    };

    /**
     * 最大分组数量
     */
    private int shardGroupMax = -1;

    /**
     * 每个分片最小值
     */
    private int eachShardMin = -1;

    /**
     * 每个分片最大值
     */
    private int eachShardMax;

    /**
     * 分片列表提供者
     */
    private Function<List<?>, List<?>> shardListSupplier;

    /**
     * 列表提供者
     */
    private Function<List<?>, List<?>> listSupplier;

    /**
     * Map提供者
     */
    private Function<Map<?, ?>, Map<?, ?>> mapSupplier;

    protected DataShardingHelper(int eachShardMax) {
        this.eachShardMax(eachShardMax);
        this.listSupplier(DEFAULT_LIST_SUPPLIER);
        this.shardListSupplier(DEFAULT_SHARD_LIST_SUPPLIER);
        this.mapSupplier(DEFAULT_MAP_SUPPLIER);
    }

    public static DataShardingHelper build() {
        return new DataShardingHelper(DEFAULT_EACH_SHARD_MAX);
    }

    public static DataShardingHelper build(int eachShardMax) {
        return new DataShardingHelper(eachShardMax);
    }

    /**
     * 设置列表提供者
     *
     * @param supplier 列表提供者
     * @return {@link DataShardingHelper}
     */
    public DataShardingHelper listSupplier(Function<List<?>, List<?>> supplier) {
        this.listSupplier = supplier;
        return this;
    }

    /**
     * 设置分片列表提供者
     *
     * @param supplier 分片列表提供者
     * @return {@link DataShardingHelper}
     */
    @Deprecated
    public DataShardingHelper shardListSupplier(Supplier<List<?>> supplier) {
        this.shardListSupplier = (list) -> supplier.get();
        return this;
    }

    public DataShardingHelper shardListSupplier(Function<List<?>, List<?>> supplier) {
        this.shardListSupplier = supplier;
        return this;
    }

    /**
     * 设置Map提供者
     *
     * @param supplier 列表提供者
     * @return {@link DataShardingHelper}
     */
    public DataShardingHelper mapSupplier(Function<Map<?, ?>, Map<?, ?>> supplier) {
        this.mapSupplier = supplier;
        return this;
    }

    public DataShardingHelper shardGroupMax(int shardGroupMax) {
        assert shardGroupMax >= 1 || shardGroupMax == -1 : "shard group max must be greater than 0 or equal -1";
        this.shardGroupMax = shardGroupMax;
        return this;
    }

    public DataShardingHelper eachShardMin(int eachShardMin) {
        assert eachShardMin >= 1 || eachShardMin == -1 : "each shard min must be greater than 0 or equal -1";
        this.eachShardMin = eachShardMin;
        return this;
    }

    public DataShardingHelper eachShardMax(int eachShardMax) {
        assert eachShardMax >= 1 || eachShardMax == -1 : "each shard max must be greater than 0 or equal -1";
        this.eachShardMax = eachShardMax;
        return this;
    }

    /**
     * 开始对指定列表进行分片
     *
     * @param list 指定列表
     * @return 分片结果
     */
    public <T> List<List<T>> sharding(List<T> list) {
        List<List<T>> shardList = newShardList();
        if (CollectionUtils.isEmpty(list)) {
            return shardList;
        }
        int total = list.size();
        EachShardCount eachShardCount = computeEachShardCount(total);
        int batch = eachShardCount.batch, begin = 0, step = eachShardCount.basic, end = step;
        int bc = 1;
        do {
            shardList.add(newShardList(list.subList(begin, end)));
            begin += step;
            bc++;
            if (bc == batch) {
                shardList.add(newShardList(list.subList(begin, total)));
                return shardList;
            }
            end += step;
        } while (end < total);
        return shardList;
    }

    public <T, R> List<R> sharding(List<T> list, Function<List<T>, List<R>> converter) {
        return flat(sharding(list), converter);
    }

    public <K, V> List<Map<K, V>> sharding(Map<K, V> map) {
        List<Map<K, V>> shardList = newShardList();
        if (MapUtils.isEmpty(map)) {
            return shardList;
        }
        int total = map.size();
        EachShardCount eachShardCount = computeEachShardCount(total);
        int batch = eachShardCount.batch, begin = 0, step = eachShardCount.basic, end = step;
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        int bc = 1;
        do {
            shardList.add(newSubMap(iterator, begin, end));
            begin += step;
            bc++;
            if (bc == batch) {
                shardList.add(newSubMap(iterator, begin, total));
                return shardList;
            }
            end += step;
        } while (end < total);
        return shardList;
    }

    /**
     * please using {@link ShardingFetcher}
     */
    @Deprecated
    public <T> List<T> sharding(int total, BiFunction<Integer, Integer, List<T>> converter) {
        EachShardCount eachShardCount = computeEachShardCount(total);
        int batch = eachShardCount.batch, begin = 0, step = eachShardCount.basic, end = step;
        List<T> shardList = newList();
        int bc = 1;
        do {
            shardList.addAll(converter.apply(begin, end));
            begin += step;
            bc++;
            if (bc == batch) {
                shardList.addAll(converter.apply(begin, total));
                return shardList;
            }
            end += step;
        } while (end < total);
        return shardList;
    }

    public <T> List<T> sharding(int total, ShardingFetcher<T> fetcher) {
        EachShardCount eachShardCount = computeEachShardCount(total);
        int batch = eachShardCount.batch, begin = 0, step = eachShardCount.basic, end = step;
        List<T> shardList = newList();
        int bc = 1;
        do {
            shardList.addAll(fetcher.apply(begin, end, bc, step));
            begin += step;
            bc++;
            if (bc == batch) {
                shardList.addAll(fetcher.apply(begin, total, bc, total - begin));
                return shardList;
            }
            end += step;
        } while (end < total);
        return shardList;
    }

    /**
     * 开始对指定列表进行分片
     *
     * @param list 指定列表
     * @return 分片结果
     */
    public <T> List<List<T>> collectionSharding(Collection<T> list) {
        List<List<T>> shardList = newShardList();
        if (CollectionUtils.isEmpty(list)) {
            return shardList;
        }
        int total = list.size();
        EachShardCount eachShardCount = computeEachShardCount(total);
        int batch = eachShardCount.batch, begin = 0, step = eachShardCount.basic, end = step;
        int bc = 1;
        Iterator<T> iterator = list.iterator();
        List<T> subList;
        do {
            subList = newList();
            for (int i = begin; i < end; i++) {
                subList.add(iterator.next());
            }
            shardList.add(subList);
            begin += step;
            bc++;
            if (bc == batch) {
                subList = newList();
                for (int i = begin; i < total; i++) {
                    subList.add(iterator.next());
                }
                shardList.add(subList);
                return shardList;
            }
            end += step;
        } while (end < total);
        return shardList;
    }

    public <T, R> List<R> collectionSharding(Collection<T> list, Function<List<T>, List<R>> converter) {
        return flat(collectionSharding(list), converter);
    }

    /**
     * 计算分片中较为合适的数据量
     *
     * @param total 指定数据量
     * @return 分片数据量
     */
    protected EachShardCount computeEachShardCount(int total) {
        if (this.shardGroupMax != -1) {
            return computeEachShardCountByShardGroup(total);
        }
        return computeEachShardCountByMax(total);
    }

    protected EachShardCount computeEachShardCountByMax(int total) {
        if (total <= this.eachShardMax) {
            return new EachShardCount(total, 1);
        }
        int shardingCount = total / this.eachShardMax;
        int remainder = total % this.eachShardMax;
        if (remainder == 0) {
            return new EachShardCount(this.eachShardMax, shardingCount);
        }
        int batchCount = shardingCount + 1;
        return new EachShardCount(total / batchCount, batchCount);
    }

    protected EachShardCount computeEachShardCountByMin(int total) {
        int shardingCount = total / this.eachShardMin;
        int remainder = total % this.eachShardMin;
        if (remainder == 0) {
            return new EachShardCount(this.eachShardMin, shardingCount);
        }
        int batchCount = shardingCount + 1;
        return new EachShardCount(total / batchCount, batchCount);
    }

    protected EachShardCount computeEachShardCountByShardGroup(int total) {
        if (total <= this.shardGroupMax) {
            if (this.eachShardMin >= 1) {
                return computeEachShardCountByMin(total);
            }
            return new EachShardCount(1, total);
        }
        int shardingCount = total / this.shardGroupMax;
        if (this.eachShardMin >= shardingCount) {
            return computeEachShardCountByMin(total);
        }
        return new EachShardCount(shardingCount, this.shardGroupMax);
    }

    protected <T> List<T> newShardList(List<T> list) {
        return cast(this.shardListSupplier.apply(list));
    }

    protected <T> List<T> newShardList() {
        return cast(this.shardListSupplier.apply(null));
    }

    protected <T> List<T> newList(List<T> list) {
        return cast(this.listSupplier.apply(list));
    }

    protected <T> List<T> newList() {
        return cast(this.listSupplier.apply(null));
    }

    protected <K, V> Map<K, V> newMap(Map<K, V> map) {
        return cast(this.mapSupplier.apply(map));
    }

    protected <K, V> Map<K, V> newMap() {
        return cast(this.mapSupplier.apply(null));
    }

    protected <K, V> Map<K, V> newSubMap(Iterator<Map.Entry<K, V>> iterator, int begin, int end) {
        Map<K, V> map = newMap();
        for (int i = begin; i < end; i++) {
            Map.Entry<K, V> entry = iterator.next();
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * 每个分片数据量
     */
    protected static class EachShardCount {

        /**
         * 基础分片数据量
         */
        public int basic;

        /**
         * 批数
         */
        public int batch;

        private EachShardCount(int basic, int batch) {
            this.basic = basic;
            this.batch = batch;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, R> R cast(T value) {
        return (R) value;
    }

    private static <T, R, C extends Collection<T>, CC extends Collection<C>> List<R> flat(CC list, Function<C, List<R>> converter) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        List<R> results = new ArrayList<>(32);
        for (C item : list) {
            List<R> target = converter.apply(item);
            if (CollectionUtils.isNotEmpty(target)) {
                results.addAll(target);
            }
        }
        return results;
    }

    @FunctionalInterface
    public interface ShardingFetcher<T> {

        List<T> apply(int begin, int end, int page, int size);
    }
}
