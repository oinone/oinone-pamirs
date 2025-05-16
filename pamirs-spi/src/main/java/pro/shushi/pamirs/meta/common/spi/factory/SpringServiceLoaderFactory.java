package pro.shushi.pamirs.meta.common.spi.factory;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import pro.shushi.pamirs.meta.common.spi.util.SpiHelper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Spring SPI工厂
 * <p>
 * 2020/8/3 1:27 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SpringServiceLoaderFactory implements ServiceLoaderFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> void loadServiceInstances(Class<T> serviceClassType, BiConsumer<Pair<String, Class<T>>, Supplier<T>> consumer) {
        Map<String, T> serviceInstanceMap = BeanDefinitionUtils.getBeansOfType(serviceClassType);
        if (null == serviceInstanceMap) {
            return;
        }
        Map<Object, String> beanNameMap = new HashMap<>();
        for (String name : serviceInstanceMap.keySet()) {
            T serviceInstance = serviceInstanceMap.get(name);
            beanNameMap.put(serviceInstance, name);
        }
        List<T> spiList = new ArrayList<>(serviceInstanceMap.values());
        AnnotationAwareOrderComparator.sort(spiList);
        for (T serviceInstance : spiList) {
            String name = beanNameMap.get(serviceInstance);
            Class<T> tClass = (Class<T>) serviceInstance.getClass();
            String extensionName = SpiHelper.fetchExtensionName(tClass);
            if (null == extensionName) {
                extensionName = name;
            }
            Pair<String, Class<T>> key = new MutablePair<>(extensionName, tClass);
            consumer.accept(key, () -> serviceInstance);
        }
    }

}
