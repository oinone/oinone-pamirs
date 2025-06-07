package pro.shushi.pamirs.meta.common.spi.factory;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import pro.shushi.pamirs.meta.common.spi.util.SpiHelper;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Java SPI工厂
 * <p>
 * 2020/8/3 1:27 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class JavaServiceLoaderFactory implements ServiceLoaderFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> void loadServiceInstances(Class<T> serviceClassType, BiConsumer<Pair<String, Class<T>>, Supplier<T>> consumer) {
        ServiceLoader<T> load = ServiceLoader.load(serviceClassType);
        List<T> spiClassList = Lists.newArrayList(load.iterator());
        AnnotationAwareOrderComparator.sort(spiClassList);
        for (T serviceInstance : spiClassList) {
            Class<T> tClass = (Class<T>) serviceInstance.getClass();
            String extensionName = SpiHelper.fetchExtensionName(tClass);
            if (null == extensionName) {
                extensionName = tClass.getName();
            }
            Pair<String, Class<T>> key = new MutablePair<>(extensionName, tClass);
            consumer.accept(key, () -> serviceInstance);
        }
    }

}
