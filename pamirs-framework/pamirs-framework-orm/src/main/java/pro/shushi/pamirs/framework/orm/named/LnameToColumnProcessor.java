package pro.shushi.pamirs.framework.orm.named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.processor.NameProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_LNAME_TO_COLUMN_MODEL_CONFIG_ERROR;

/**
 * java名称转数据库列名转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class LnameToColumnProcessor implements NameProcessor {

    @Override
    public Map<String, Object> convert(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        if (!Models.modelDirective().isDoColumn(origin)) {
            return origin;
        }
        String lname = fieldConfig.getLname();
        if (origin.containsKey(lname) && !StringUtils.isBlank(fieldConfig.getColumn())) {
            origin.put(fieldConfig.getColumn(), origin.get(lname));
        }
        return origin;
    }

    @Override
    public Map<String, Object> convert(String model, Map<String, Object> origin) {
        if (null == origin) {
            return null;
        }
        if (!Models.modelDirective().isDoColumn(origin)) {
            return origin;
        }
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
        if (null == modelConfig) {
            throw PamirsException.construct(BASE_LNAME_TO_COLUMN_MODEL_CONFIG_ERROR)
                    .appendMsg("model:" + model).errThrow();
        }
        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();
        for (ModelFieldConfig modelFieldConfig : modelFieldConfigList) {
            convert(modelFieldConfig, origin);
        }
        return origin;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<Map<String, Object>> convert(String model, List<Map<String, Object>> map) {
        if (null == map) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map t : map) {
            result.add(convert(model, t));
        }
        return result;
    }

}
