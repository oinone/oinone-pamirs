package pro.shushi.pamirs.framework.compare;

import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 差量接口
 * <p>
 * 2020/11/19 10:59 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface DiffService {

    /**
     * 差量标记
     *
     * @param t   元数据
     * @param <T> 元数据类型
     */
    <T extends MetaBaseModel> void hash(T t);

    /**
     * 是否存在差量
     *
     * @param t   元数据
     * @param <T> 元数据类型
     * @return 序列化元数据
     */
    <T extends MetaBaseModel> boolean diff(T t);

}
