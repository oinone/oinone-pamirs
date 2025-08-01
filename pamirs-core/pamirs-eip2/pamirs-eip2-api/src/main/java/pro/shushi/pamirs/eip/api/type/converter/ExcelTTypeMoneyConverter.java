package pro.shushi.pamirs.eip.api.type.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
@Slf4j
public class ExcelTTypeMoneyConverter implements ExcelTTypeConverter {

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.MONEY.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        try {
            switch (excelTTypeDescriptor.getOriginType()) {
                case "datetime":
                case "year":
                case "date":
                case "time": {
                    log.debug("can not convert date type {} to money", value);
                    return defaultValue(excelTTypeDescriptor);
                }
                default: {
                    String moneyValue = extractAmountString(value);
                    if (StringUtils.isBlank(moneyValue)) {
                        log.debug("can not convert {} to money", value);
                        return defaultValue(excelTTypeDescriptor);
                    }
                    return new BigDecimal(moneyValue).toPlainString();
                }
            }
        } catch (Exception e) {
            log.debug("can not convert {} to money", value, e);
            return defaultValue(excelTTypeDescriptor);
        }
    }

    /**
     * 提取字符串中的金额（去除货币符号和千位分隔符），保留符号、小数点
     */
    @Nullable
    public static String extractAmountString(@Nullable String value) {
        if (StringUtils.isBlank(value)) return value;

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

        return sb.toString();
    }
}
