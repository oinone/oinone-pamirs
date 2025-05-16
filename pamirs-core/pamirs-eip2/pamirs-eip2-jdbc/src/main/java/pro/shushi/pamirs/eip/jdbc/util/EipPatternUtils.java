package pro.shushi.pamirs.eip.jdbc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EipPatternUtils
 *
 * @author yakir on 2024/09/23 15:44.
 */
public class EipPatternUtils {

    public static final Pattern PLACEHOLDER = Pattern.compile("\\{([^}]*)}");

    public static Matcher matcher(String sql) {
        return PLACEHOLDER.matcher(sql);
    }

}
