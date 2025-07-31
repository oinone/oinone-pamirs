package pro.shushi.pamirs.eip.api.type.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
@Slf4j
public class ExcelTTypeMoneyConverter implements ExcelTTypeConverter {

    public static final Pattern MONEY_PATTERN = Pattern.compile("[-+]?((\\d{1,3}(,\\d{3})+)|\\d+)(\\.\\d+)?");

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.MONEY.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        try {
            switch (excelTTypeDescriptor.getOriginType()) {
                case "money":
                    return new BigDecimal(extractAmountString(value)).toPlainString();
                case "binary": {
                    try {
                        if (value.length() == 64) {
                            long bits = Long.parseUnsignedLong(value, 2);
                            return Double.longBitsToDouble(bits) + "";
                        } else if (value.length() == 32) {
                            int bits = Integer.parseUnsignedInt(value, 2);
                            return Float.intBitsToFloat(bits) + "";
                        }
                    } catch (Exception e) {
                        log.debug("error to converter binary to money", e);
                    }
                    BigDecimal bigDecimal = new BigDecimal(extractAmountString(value));
                    return bigDecimal.toPlainString();
                }
                case "bool": {
                    if ("true".equals(value)) {
                        return "1";
                    } else if ("false".equals(value)) {
                        return "0";
                    }
                    throw new IllegalArgumentException(value + " is not a boolean");
                }
                case "datetime":
                case "year":
                case "date":
                case "time": {
                    value = value.replaceAll("\\D+", "");
                    break;
                }
                default:
                    return new BigDecimal(extractAmountString(value)).toPlainString();
            }
        } catch (Exception e) {
            log.debug("can not convert {} to money", value, e);
            String[] split = value.split("\\.");
            StringBuilder sb = new StringBuilder();
            boolean hasPot = false;
            for (int i = 0; i < split.length; i++) {
                String s = split[i].replaceAll("\\D+", "");
                sb.append(s);
                if (StringUtils.isNotBlank(s) && !hasPot && i != split.length - 1) {
                    sb.append(".");
                    hasPot = true;
                }
            }
            value = sb.toString();
        }

        if (StringUtils.isBlank(value)) {
            log.debug("input a empty string to money, use default value");
            return defaultValue();
        }
        return value;
    }

    @Override
    public String defaultValue() {
        return "0.00";
    }

    /**
     * 提取字符串中的金额（去除货币符号和千位分隔符），保留符号、小数点
     */
    public static String extractAmountString(String input) {
        if (StringUtils.isBlank(input)) return input;
        Matcher matcher = MONEY_PATTERN.matcher(input);

        if (matcher.find()) {
            String raw = matcher.group();
            return raw.replaceAll(",", "");
        }

        return null;
    }
}
