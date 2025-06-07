package pro.shushi.pamirs.framework.orm.converter.entity.type;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Map;

/**
 * 持久层字符串转换服务
 *
 * @author Adamancy Zhang at 10:55 on 2024-10-09
 */
@Component
public class PersistenceStringConverter {

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        String lname = fieldConfig.getLname();
        Object value = origin.get(lname);
        if (null == value) {
            return;
        }
        if (String.class.getName().equals(fieldConfig.getLtype())) {
            String stringValue = TypeUtils.prepareString(value);
            if (stringValue != null) {
                origin.put(lname, stringValue);
            }
        }
    }
}
