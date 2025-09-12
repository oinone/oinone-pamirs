package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class BooleanConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.isBoolType(ttype.value());
    }

    @Override
    public Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        if ("TRUE".equalsIgnoreCase(value) || "1".equals(value) || "是".equals(value) || "Y".equalsIgnoreCase(value)) {
            return boolCaseModelValue(Boolean.TRUE, quickFillingField.getModelConfigField());
        } else if ("FALSE".equalsIgnoreCase(value) || "0".equals(value) || "否".equals(value) || "N".equalsIgnoreCase(value)) {
            return boolCaseModelValue(Boolean.FALSE, quickFillingField.getModelConfigField());
        }

        failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
        return null;
    }

    private Object boolCaseModelValue(boolean value, ModelFieldConfig modelFieldConfig) {
        return value;
    }
}
