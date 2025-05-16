package pro.shushi.pamirs.framework.common.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.api.core.compute.definition.ValueComputer;
import pro.shushi.pamirs.meta.api.core.orm.serialize.SerializeProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldFix;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * 字段默认值计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service
@Component
public class DefaultValueComputer implements ValueComputer {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> void compute(ModelFieldConfig modelField, T data) {
        // 计算默认值
        String lname = modelField.getLname();
        Object fieldValue = FieldUtils.getFieldValue(data, lname);
        if (null == fieldValue) {
            String fieldType = modelField.getLtype();
            String fieldTypeT = modelField.getLtypeT();
            Class<?> fieldClass = TypeUtils.getClass(fieldType);
            Object defaultValue;
            if (StringUtils.isNotBlank(modelField.getStoreSerialize()) && !Field.serialize.NON.equals(modelField.getStoreSerialize())
                    || (null != modelField.getMulti() && modelField.getMulti())) {
                defaultValue = Spider.getDefaultExtension(SerializeProcessor.class)
                        .deserialize(modelField.getStoreSerialize(), fieldType, fieldTypeT, modelField.getFormat(), modelField.getDefaultValue());
            } else if (TtypeEnum.ENUM.value().equals(modelField.getTtype()) && TypeUtils.isIEnumClass(fieldClass, fieldType)) {
                defaultValue = Enums.getEnumByValue((Class) fieldClass, modelField.getDefaultValue());
            } else {
                defaultValue = TypeUtils.valueOfPrimary(fieldType, modelField.getDefaultValue(), FieldFix.fixFormat(modelField.getModelField()));
            }
            FieldUtils.setFieldValue(data, lname, defaultValue);
        }
    }

}
