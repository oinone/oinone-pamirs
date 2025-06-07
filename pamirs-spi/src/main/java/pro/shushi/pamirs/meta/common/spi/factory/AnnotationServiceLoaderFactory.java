package pro.shushi.pamirs.meta.common.spi.factory;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import pro.shushi.pamirs.meta.common.spi.ExtensionServiceLoader;
import pro.shushi.pamirs.meta.common.spi.Holder;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.path.SpiClassPathApi;
import pro.shushi.pamirs.meta.common.spi.util.SpiHelper;
import pro.shushi.pamirs.meta.common.stl.ConcurrentHashSet;
import pro.shushi.pamirs.meta.common.util.SpiClassScanner;
import pro.shushi.pamirs.meta.common.util.SpiListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 注解 SPI工厂
 * <p>
 * 2020/8/3 1:27 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AnnotationServiceLoaderFactory implements ServiceLoaderFactory {

    private final static Holder<Set<Class<?>>> spiClassesHolder = new Holder<>();

    private static Set<Class<?>> init() {
        Set<Class<?>> spiClasses = spiClassesHolder.get();
        if (null == spiClasses) {
            synchronized (spiClassesHolder) {
                spiClasses = spiClassesHolder.get();
                if (null == spiClasses) {
                    spiClasses = new ConcurrentHashSet<>();
                    List<String> paths = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class).getExtension().path();
                    Set<Class<?>> classSet = SpiClassScanner.scan(false, SpiListUtils.toArray(paths), SPI.Service.class);
                    spiClasses.addAll(classSet);
                    spiClassesHolder.set(spiClasses);
                }
            }
        }
        return spiClasses;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void loadServiceInstances(Class<T> serviceClassType, BiConsumer<Pair<String, Class<T>>, Supplier<T>> consumer) {
        List<Class<?>> spiClassList = new ArrayList<>(init());
        AnnotationAwareOrderComparator.sort(spiClassList);
        for (Class<?> spiClass : spiClassList) {
            if (serviceClassType.isAssignableFrom(spiClass)) {
                Pair<String, Class<T>> key = new MutablePair<>(SpiHelper.fetchExtensionName(spiClass), (Class<T>) spiClass);
                consumer.accept(key, () -> {
                    try {
                        //noinspection unchecked
                        return (T) spiClass.newInstance();
                    } catch (Throwable t) {
                        throw new IllegalStateException("Extension instance (spi class: " + serviceClassType + ", extend class: " +
                                spiClass + ") couldn't be instantiated: " + t.getMessage(), t);
                    }
                });
            }
        }
    }

    @SuppressWarnings("unused")
    public static <T> boolean withExtensionAnnotation(Class<T> type) {
        return type.isAnnotationPresent(SPI.class);
    }

}
