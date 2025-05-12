package pro.shushi.pamirs.meta.api.core.orm.convert;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 字段转换API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FieldConverter {

    /**
     * 入转换
     *
     * @param fieldConfig 字段配置
     * @param fieldValue  字段值
     * @return 转换结果
     */
    <T> T in(ModelFieldConfig fieldConfig, Object fieldValue);

    /**
     * 出转换
     *
     * @param fieldConfig 字段配置
     * @param fieldValue  字段值
     * @return 转换结果
     */
    <T> T out(ModelFieldConfig fieldConfig, Object fieldValue);

}
