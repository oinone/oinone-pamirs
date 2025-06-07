package pro.shushi.pamirs.framework.orm.client.converter.field;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.serialize.SerializeProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * 前端序列化模板
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientSerializeConvertor {

    @SuppressWarnings({"rawtypes"})
    public Object in(ModelFieldConfig fieldConfig, Object fieldValue) {
        if (StringUtils.isBlank(fieldConfig.getRequestSerialize())) {
            return fieldValue;
        }
        boolean isComplexType = !TypeUtils.isBaseType(fieldConfig.getLtype());
        boolean isBaseValue = TypeUtils.isBaseType(fieldValue.getClass());
        if (isComplexType && isBaseValue) {
            SerializeProcessor serializeProcessor = Spider.getDefaultExtension(SerializeProcessor.class);
            String valueString = TypeUtils.stringValueOf(fieldValue);
            fieldValue = serializeProcessor.deserialize(fieldConfig.getRequestSerialize(), fieldConfig.getLtype(),
                    fieldConfig.getLtypeT(), fieldConfig.getFormat(), valueString);
        }
        return fieldValue;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object out(ModelFieldConfig fieldConfig, Object fieldValue) {
        if (StringUtils.isBlank(fieldConfig.getRequestSerialize())) {
            return fieldValue;
        }
        boolean isBasicType = TtypeEnum.isBasicType(fieldConfig.getTtype());
        boolean isComplexValue = !TypeUtils.isBaseType(fieldValue.getClass());
        if (isBasicType && isComplexValue) {
            SerializeProcessor serializeProcessor = Spider.getDefaultExtension(SerializeProcessor.class);
            String ltype = fieldConfig.getLtype();
            String ltypeT = fieldConfig.getLtypeT();
            if (isMulti(fieldConfig)) {
                ltype = ltypeT;
            }
            fieldValue = serializeProcessor.serialize(fieldConfig.getRequestSerialize(), ltype, fieldValue);
        }
        return fieldValue;
    }

    private boolean isMulti(ModelFieldConfig fieldConfig) {
        return null != fieldConfig.getMulti() && fieldConfig.getMulti();
    }

}
