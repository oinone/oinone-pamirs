package pro.shushi.pamirs.boot.web.service.impl.filling;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueTransformer;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Set;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class BooleanTransformer implements QuickFillingValueTransformer {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.isBoolType(ttype.value());
    }

    @Override
    public Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if ("true".equalsIgnoreCase(value)) {
            return boolCaseModelValue(Boolean.TRUE, quickFillingField.getModelConfigField());
        } else if ("false".equalsIgnoreCase(value)) {
            return boolCaseModelValue(Boolean.FALSE, quickFillingField.getModelConfigField());
        }

        return null;
    }

    private Object boolCaseModelValue(boolean value, ModelFieldConfig modelFieldConfig) {
        return value;
    }
}
