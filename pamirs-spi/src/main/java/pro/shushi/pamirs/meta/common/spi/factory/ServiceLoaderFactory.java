package pro.shushi.pamirs.meta.common.spi.factory;

import org.apache.commons.lang3.tuple.Pair;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * SPI工厂
 * <p>
 * 2020/8/3 1:27 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface ServiceLoaderFactory {

    /**
     * 加载服务扩展实例
     *
     * @param serviceClassType spi class
     * @param consumer         加载消费者
     * @param <T>              spi接口
     */
    <T> void loadServiceInstances(Class<T> serviceClassType, BiConsumer<Pair<String, Class<T>>, Supplier<T>> consumer);

}
