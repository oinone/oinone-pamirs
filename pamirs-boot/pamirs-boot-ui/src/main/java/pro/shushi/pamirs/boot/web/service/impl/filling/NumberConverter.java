package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.math.BigDecimal;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class NumberConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.isNumericType(ttype.value()) || TtypeEnum.MONEY.equals(ttype);
    }

    @Override
    public Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        if (!originIsNumber(value)) {
            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE, value);
            return null;
        }

        BigDecimal number = new BigDecimal(value);
        ModelFieldConfig modelConfigField = quickFillingField.getModelConfigField();

        return numberCaseModelValue(number, modelConfigField);
    }

    private Object numberCaseModelValue(BigDecimal number, ModelFieldConfig modelFieldConfig) {
        return number;
    }

    private boolean originIsNumber(String value) {
        if (StringUtils.isBlank(value)) return false;
        return value.matches("^-?(\\d+(\\.\\d+)?|\\.\\d+)$");
    }
}
