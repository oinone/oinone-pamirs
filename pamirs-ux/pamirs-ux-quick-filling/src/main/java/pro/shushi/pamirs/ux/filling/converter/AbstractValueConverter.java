package pro.shushi.pamirs.ux.filling.converter;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gesi at 16:36 on 2025/9/11
 */
@Slf4j
public abstract class AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public Object convert(QuickFillingContext context, String value) {
        ModelFieldConfig modelFieldConfig = context.getModelFieldConfig();
        if (Boolean.TRUE.equals(modelFieldConfig.getMulti())) {
            List<Object> results = new ArrayList<>();
            String[] valueList = value.split(",");
            for (String valueItem : valueList) {
                if (StringUtils.isBlank(valueItem)) {
                    continue;
                }
                valueItem = valueItem.trim();
                Object target = convert0(context, valueItem);
                if (context.isFailed()) {
                    return null;
                }
                results.add(target);
            }
            return results;
        }
        return convert0(context, value);
    }

    protected Object convert0(QuickFillingContext context, String value) {
        try {
            return singleValueConvert(context, value);
        } catch (Exception e) {
            log.error("自动填报类型转换失败", e);
            context.fail();
            return null;
        }
    }

    protected abstract Object singleValueConvert(QuickFillingContext context, String value);

}
