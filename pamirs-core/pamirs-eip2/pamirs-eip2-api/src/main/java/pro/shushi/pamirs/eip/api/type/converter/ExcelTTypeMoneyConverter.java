package pro.shushi.pamirs.eip.api.type.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
@Slf4j
public class ExcelTTypeMoneyConverter implements ExcelTTypeConverter {

    public static final Set<String> CURRENCY_SYMBOL_SET = Collections.unmodifiableSet(initCurrencySymbol());

    public static boolean originIsNumber(String value) {
        if (StringUtils.isBlank(value)) return false;
        value = extractAmountString(value);
        return value.matches("^-?(\\d+(\\.\\d+)?|\\.\\d+)$");
    }

    /**
     * 初始化所有货币符号
     */
    private static Set<String> initCurrencySymbol() {
        Set<String> symbolSet = new HashSet<>();
        Set<Currency> currencies = Currency.getAvailableCurrencies();
        Locale[] locales = Locale.getAvailableLocales();

        for (Currency currency : currencies) {
            String symbol = null;

            // 找到第一个能提供非货币代码形式的符号的 Locale
            for (Locale locale : locales) {
                try {
                    String s = currency.getSymbol(locale);
                    if (!s.equals(currency.getCurrencyCode())) {
                        symbol = s;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }

            if (symbol != null) {
                symbolSet.add(symbol);
            }
        }
        return symbolSet;
    }

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.MONEY.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        try {
            switch (excelTTypeDescriptor.getOriginType()) {
                case "bool":
                case "datetime":
                case "year":
                case "date":
                case "time": {
                    log.debug("can not convert {} type {} to a number", excelTTypeDescriptor.getOriginType(), value);
                    return defaultValue(excelTTypeDescriptor);
                }
                default: {
                    String moneyValue = extractAmountString(value);
                    if (StringUtils.isBlank(moneyValue)) {
                        log.debug("can not convert {} to a number", value);
                        return defaultValue(excelTTypeDescriptor);
                    }
                    return new BigDecimal(moneyValue).toPlainString();
                }
            }
        } catch (Exception e) {
            log.debug("can not convert {} to a number", value, e);
            return defaultValue(excelTTypeDescriptor);
        }
    }

    /**
     * 提取字符串中的纯数字（去除货币符号和千位分隔符），保留符号、小数点
     */
    public static String extractAmountString(String value) {
        if (StringUtils.isBlank(value)) return value;

        value = value
                .replaceAll(" ", "")
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .replaceAll(",", "")
                .replaceAll("，", "")
        ;

        for (String symbol : CURRENCY_SYMBOL_SET) {
            if (value.startsWith(symbol)) {
                value = value.substring(symbol.length());
            }
            if (value.endsWith(symbol)) {
                value = value.substring(0, value.length() - symbol.length());
            }
        }

        return value;
    }
}
