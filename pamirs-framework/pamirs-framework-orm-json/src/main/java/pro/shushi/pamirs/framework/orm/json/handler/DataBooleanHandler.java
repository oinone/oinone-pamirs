package pro.shushi.pamirs.framework.orm.json.handler;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.Map;

/**
 * 布尔处理
 *
 * @author Adamancy Zhang at 13:37 on 2025-02-27
 */
public class DataBooleanHandler {

    public static void toBoolean(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value || value instanceof Boolean) {
            return;
        }
        if (value instanceof String) {
            if (Boolean.TRUE.toString().equals(value)) {
                origin.put(fieldConfig.getLname(), Boolean.TRUE);
            }
            if (Boolean.FALSE.toString().equals(value)) {
                origin.put(fieldConfig.getLname(), Boolean.FALSE);
            }
        }
    }
}
