package pro.shushi.pamirs.framework.common.id;


import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * UidGeneratorFactory
 *
 * @author yakir on 2019/10/18 17:24.
 */
public class UidGeneratorFactory {

    public final static String CACHED_UID_GENERATOR_NAME = "cache";

    public static UidGenerator getCachedUidGenerator() {
        return Spider.getExtension(UidGenerator.class, CACHED_UID_GENERATOR_NAME);
    }

    public static UidGenerator getDefaultUidGenerator() {
        return Spider.getDefaultExtension(UidGenerator.class);
    }

}
