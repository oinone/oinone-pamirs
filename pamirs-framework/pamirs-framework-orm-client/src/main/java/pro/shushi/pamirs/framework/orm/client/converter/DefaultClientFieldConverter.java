package pro.shushi.pamirs.framework.orm.client.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.client.converter.field.ClientEnumNamedConverter;
import pro.shushi.pamirs.framework.orm.client.converter.field.ClientSerializeConvertor;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientFieldConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;

/**
 * 前后端字段转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings("unchecked")
@SPI.Service
@Component
public class DefaultClientFieldConverter implements ClientFieldConverter {

    @Resource
    private ClientSerializeConvertor fieldSerializeConvertor;

    @Resource
    private ClientEnumNamedConverter fieldEnumNameConverter;

    @Override
    public <T> T in(ModelFieldConfig fieldConfig, Object fieldValue) {
        if (null == fieldValue) {
            return null;
        }
        // 前端枚举字段处理
        fieldValue = fieldEnumNameConverter.in(fieldConfig, fieldValue);
        // 输入字段一致性转换
        fieldValue = fieldSerializeConvertor.in(fieldConfig, fieldValue);
        return (T) fieldValue;
    }

    @Override
    public <T> T out(ModelFieldConfig fieldConfig, Object fieldValue) {
        if (null == fieldValue) {
            return null;
        }
        // 前端枚举字段处理
        fieldValue = fieldEnumNameConverter.out(fieldConfig, fieldValue);
        // 输出字段一致性转换
        fieldValue = fieldSerializeConvertor.out(fieldConfig, fieldValue);
        return (T) fieldValue;
    }

}
