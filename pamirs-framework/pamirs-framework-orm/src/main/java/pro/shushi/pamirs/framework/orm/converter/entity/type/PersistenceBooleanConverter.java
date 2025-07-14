package pro.shushi.pamirs.framework.orm.converter.entity.type;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.Map;

/**
 * 持久层布尔转换服务
 *
 * @author Adamancy Zhang at 15:22 on 2023-06-27
 */
@Component
public class PersistenceBooleanConverter {

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        if (value instanceof Byte) {
            origin.put(fieldConfig.getLname(), ((Byte) value).intValue() != 0);
        } else if (value instanceof Number) {
            origin.put(fieldConfig.getLname(), !"0".equals(String.valueOf(value)));
        }
    }
}
