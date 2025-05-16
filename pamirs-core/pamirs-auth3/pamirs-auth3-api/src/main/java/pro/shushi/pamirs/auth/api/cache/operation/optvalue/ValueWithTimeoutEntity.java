package pro.shushi.pamirs.auth.api.cache.operation.optvalue;

import java.util.concurrent.TimeUnit;

/**
 * 带超时时间的Value类型缓存实体
 *
 * @author Adamancy Zhang at 15:14 on 2024-04-12
 */
public class ValueWithTimeoutEntity<V> extends ValueEntity<V> {

    private final long timeout;

    private final TimeUnit unit;

    public ValueWithTimeoutEntity(String key, V value, long timeout, TimeUnit unit) {
        super(key, value);
        this.timeout = timeout;
        this.unit = unit;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }
}
