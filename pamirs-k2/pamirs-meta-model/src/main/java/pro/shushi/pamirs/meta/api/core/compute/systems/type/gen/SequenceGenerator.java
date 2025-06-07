package pro.shushi.pamirs.meta.api.core.compute.systems.type.gen;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 序列 生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SequenceGenerator<T> {

    /**
     * 序列生成
     *
     * @param sequence   序列生成器类型
     * @param configCode 序列生成配置编码
     * @return 返回值
     * @see pro.shushi.pamirs.meta.enmu.SequenceEnum 序列生成器类型
     */
    T generate(String sequence, String configCode);

}
