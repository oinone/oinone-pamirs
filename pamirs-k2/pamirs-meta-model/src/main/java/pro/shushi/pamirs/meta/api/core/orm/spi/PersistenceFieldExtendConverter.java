package pro.shushi.pamirs.meta.api.core.orm.spi;

import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 后端字段处理扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PersistenceFieldExtendConverter {

    /**
     * 入转换
     *
     * @param context     上下文
     * @param fieldConfig 字段配置
     * @param data        对象
     */
    void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data);

    /**
     * 出转换
     *
     * @param context     上下文
     * @param fieldConfig 字段配置
     * @param data        对象
     */
    void out(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data);

}
