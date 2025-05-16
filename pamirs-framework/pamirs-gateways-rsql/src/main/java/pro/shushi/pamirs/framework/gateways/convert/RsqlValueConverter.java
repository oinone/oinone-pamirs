package pro.shushi.pamirs.framework.gateways.convert;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * Rsql值转换
 *
 * @author Adamancy Zhang at 15:09 on 2021-09-03
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RsqlValueConverter {

    /**
     * 是否匹配转换规则
     *
     * @param modelField 模型字段
     * @return 是否匹配
     */
    boolean match(ModelFieldConfig modelField);

    /**
     * 值转换
     *
     * @param modelField 模型字段
     * @param argument   参数
     * @return 新值
     */
    Object convert(ModelFieldConfig modelField, Object argument);
}
