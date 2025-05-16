package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 忽略方言SQL解析
 *
 * @author Adamancy Zhang at 14:51 on 2024-11-07
 */
public class DialectIgnoredHintApi implements AutoCloseable {

    private static final TransmittableThreadLocal<Boolean> storage = new TransmittableThreadLocal<>();

    public static boolean isIgnored() {
        Boolean isIgnored = storage.get();
        if (isIgnored == null) {
            return false;
        }
        return isIgnored;
    }

    public static DialectIgnoredHintApi ignored() {
        storage.set(Boolean.TRUE);
        return new DialectIgnoredHintApi();
    }

    @Override
    public void close() throws Exception {
        storage.remove();
    }
}
