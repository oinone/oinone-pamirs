package pro.shushi.pamirs.meta.api.core.orm.convert;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 数据转换API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DataConverter {

    String BE = "BE";

    /**
     * 入转换
     *
     * @param model 模型编码
     * @param obj   数据对象
     * @return 转换结果
     */
    <T> T in(String model, Object obj);

    /**
     * 出转换
     *
     * @param model 模型编码
     * @param obj   数据对象
     * @return 转换结果
     */
    <T> T out(String model, Object obj);

}
