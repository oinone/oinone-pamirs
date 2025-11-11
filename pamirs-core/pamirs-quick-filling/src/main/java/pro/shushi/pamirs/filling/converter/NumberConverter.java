package pro.shushi.pamirs.filling.converter;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
public class NumberConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new NumberConverter();

    private static final Pattern PATTERN = Pattern.compile("^-?(\\d+(\\.\\d+)?|\\.\\d+)$");

    @Override
    public Object singleValueConvert(QuickFillingContext context, String value) {
        if (!originIsNumber(value)) {
            context.fail();
            return null;
        }
        BigDecimal number = new BigDecimal(value);
        Object returnValue = numberCaseModelValue(context, number);
        if (returnValue == null) {
            context.fail();
        }
        return returnValue;
    }

    private Object numberCaseModelValue(QuickFillingContext context, BigDecimal number) {
        String ltype = context.getLtype();
        if (Integer.class.getName().equals(ltype)) {
            return number.intValue();
        } else if (Long.class.getName().equals(ltype)) {
            return number.longValue();
        } else if (Double.class.getName().equals(ltype)) {
            return number.doubleValue();
        } else if (Float.class.getName().equals(ltype)) {
            return number.floatValue();
        } else if (java.math.BigDecimal.class.getName().equals(ltype)) {
            if (TtypeEnum.MONEY.value().equals(ltype)) {
                ModelFieldConfig modelFieldConfig = context.getModelFieldConfig();
                if (modelFieldConfig.getDecimal() != null) {
                    return number.setScale(modelFieldConfig.getDecimal(), RoundingMode.HALF_UP);
                }
            }
            return number;
        } else if (java.math.BigInteger.class.getName().equals(ltype)) {
            return new BigInteger(number.intValue() + "");
        }
        return number.toPlainString();
    }

    private boolean originIsNumber(String value) {
        return PATTERN.matcher(value).matches();
    }
}
