package pro.shushi.pamirs.message.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

public class CaseFormatUtil {

    public static String lowerCamelToLowerUnderscore(String s){
        if (StringUtils.isBlank(s)){
            return null;
        }
        return CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(s);
    }

    public static String lowerUnderscoreToLowerCamel(String s){
        if (StringUtils.isBlank(s)){
            return null;
        }
        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(s);
    }
}
