package pro.shushi.pamirs.framework.orm.converter.entity.extend;

import pro.shushi.pamirs.meta.api.core.orm.spi.PersistenceFieldExtendConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeOp;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Map;

/**
 * 字段数据处理跳过判断
 * <p>
 * 暂未启用
 * 2021/2/20 10:00 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
//@SPI.Service
//@Component
public class PersistenceFieldSkipConverter implements PersistenceFieldExtendConverter {

    @Override
    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {
        Object value = data.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        Class<?> ltypeClazz = TypeUtils.getClass(fieldConfig.getLtype());
        if (TypeUtils.isPrimitive(fieldConfig.getLtype()) && ltypeClazz.equals(value.getClass())) {
            context.setOp(FieldComputeOp.continueNextField);
        }
    }

    @Override
    public void out(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {
        in(context, fieldConfig, data);
    }

}
