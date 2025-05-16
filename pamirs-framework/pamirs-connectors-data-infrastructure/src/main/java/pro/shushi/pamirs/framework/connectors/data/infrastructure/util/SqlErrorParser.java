package pro.shushi.pamirs.framework.connectors.data.infrastructure.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql 错误解析
 * <p>
 * 2020/7/10 4:08 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
public class SqlErrorParser {

    public static Pattern pattern = Pattern.compile("Error\\sexecuting:\\s" + "(.*?)" + ".\\s{2}Cause:\\s", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static String fetchDdlErrorCommand(String error) {
        if (StringUtils.isBlank(error)) {
            return null;
        }
        // "Error executing: " + command + ".  Cause: "
        Matcher matcher = pattern.matcher(error);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
