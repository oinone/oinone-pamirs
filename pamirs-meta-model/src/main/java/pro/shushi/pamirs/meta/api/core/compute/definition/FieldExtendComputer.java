package pro.shushi.pamirs.meta.api.core.compute.definition;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 字段扩展计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FieldExtendComputer<D, T> extends CommonApi {

    /**
     * 字段计算
     *
     * @param meta  元数据
     * @param model 模块编码
     * @param field 字段编码
     * @param data  实体数据
     * @return 计算结果
     */
    Result<Void> compute(D meta, String model, String field, T data);

}
