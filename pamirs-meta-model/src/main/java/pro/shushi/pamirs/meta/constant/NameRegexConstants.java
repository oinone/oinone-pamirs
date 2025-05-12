package pro.shushi.pamirs.meta.constant;

import java.util.regex.Pattern;

/**
 * 2020/6/22 9:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class NameRegexConstants {

    public final static Pattern ONLY_LETTER_AND_NUMBER_AND_UNDERLINE = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9]$");

}
