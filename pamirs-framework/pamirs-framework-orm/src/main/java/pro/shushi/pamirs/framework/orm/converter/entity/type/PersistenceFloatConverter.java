package pro.shushi.pamirs.framework.orm.converter.entity.type;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.util.NumberUtils;

import java.util.Map;

/**
 * 持久层BigDecimal转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceFloatConverter {

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        if(value instanceof Number){
            origin.put(fieldConfig.getLname(), NumberUtils.value((Number) value, fieldConfig.getLtype()));
        }
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        if(value instanceof Number){
            origin.put(fieldConfig.getLname(), NumberUtils.value((Number) value, fieldConfig.getLtype()));
        }
    }

}
