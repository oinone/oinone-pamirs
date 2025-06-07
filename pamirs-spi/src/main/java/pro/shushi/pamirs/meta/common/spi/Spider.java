package pro.shushi.pamirs.meta.common.spi;

/**
 * SPI快捷入口
 * <p>
 * 蜘蛛侠
 * 2020/8/6 7:58 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class Spider {

    public static <T> ExtensionServiceLoader<T> getLoader(Class<T> type) {
        return ExtensionServiceLoader.getExtensionLoader(type);
    }

    public static <T> T getExtension(Class<T> type, String name) {
        return ExtensionServiceLoader.getExtension(type, name);
    }

    public static <T> T getDefaultExtension(Class<T> type) {
        return getLoader(type).getDefaultExtension();
    }

}
