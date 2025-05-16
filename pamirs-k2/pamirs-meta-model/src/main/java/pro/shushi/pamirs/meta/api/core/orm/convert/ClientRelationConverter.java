package pro.shushi.pamirs.meta.api.core.orm.convert;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 关系字段转换API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ClientRelationConverter {

    /**
     * 结果处理
     *
     * @param model  模型编码
     * @param result 结果
     * @return 结果
     */
    Object resultHandler(String model, Object result);

}
