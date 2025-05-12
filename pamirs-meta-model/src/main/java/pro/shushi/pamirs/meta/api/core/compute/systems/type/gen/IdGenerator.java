package pro.shushi.pamirs.meta.api.core.compute.systems.type.gen;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * Id 生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface IdGenerator<T> {

    /**
     * ID生成
     *
     * @param keyGenerator ID序列生成器
     * @return ID
     */
    T generate(String keyGenerator);

}
