package pro.shushi.pamirs.framework.connectors.cdn.factory;

/**
 * cdn config router
 *
 * @author Adamancy Zhang at 11:50 on 2023-08-14
 */
public class CdnConfigRouter implements AutoCloseable {

    private final static ThreadLocal<String> KEY = new ThreadLocal<>();

    public CdnConfigRouter(String key) {
        KEY.set(key);
    }

    public static CdnConfigRouter use(String key) {
        return new CdnConfigRouter(key);
    }

    public static String get() {
        return KEY.get();
    }

    @Override
    public void close() {
        KEY.remove();
    }
}
