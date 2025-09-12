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
import java.math.BigInteger;
import java.math.RoundingMode;

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

        Object returnValue = numberCaseModelValue(number, modelConfigField);
        if (returnValue == null) {
            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE, value);
        }
        return returnValue;
    }

    private Object numberCaseModelValue(BigDecimal number, ModelFieldConfig modelFieldConfig) {
        if (Integer.class.getName().equals(modelFieldConfig.getLtype())) {
            return number.intValue();
        } else if (Long.class.getName().equals(modelFieldConfig.getLtype())) {
            return number.longValue();
        } else if (Double.class.getName().equals(modelFieldConfig.getLtype())) {
            return number.doubleValue();
        } else if (Float.class.getName().equals(modelFieldConfig.getLtype())) {
            return number.floatValue();
        } else if (Short.class.getName().equals(modelFieldConfig.getLtype())) {
            return number.shortValue();
        } else if (Byte.class.getName().equals(modelFieldConfig.getLtype())) {
            return number.byteValue();
        } else if (java.math.BigDecimal.class.getName().equals(modelFieldConfig.getLtype())) {
            if (TtypeEnum.MONEY.value().equals(modelFieldConfig.getTtype())) {
                if (modelFieldConfig.getDecimal() != null) {
                    return number.setScale(modelFieldConfig.getDecimal(), RoundingMode.DOWN);
                }
            }
            return number;
        } else if (java.math.BigInteger.class.getName().equals(modelFieldConfig.getLtype())) {
            return new BigInteger(number.intValue() + "");
        }

        return null;
    }

    private boolean originIsNumber(String value) {
        if (StringUtils.isBlank(value)) return false;
        return value.matches("^-?(\\d+(\\.\\d+)?|\\.\\d+)$");
    }
}
