package pro.shushi.pamirs.framework.compute.system.check.util;

import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.regex.Pattern;

/**
 * 主要针对字符串
 *
 * @author zz
 */
public class CheckUtils {

    public final static Pattern ONLY_LOWERCASE_LETTER_PATTERN = Pattern.compile("^[a-z]{0,8}$");

    public final static Pattern ONLY_LOWERCASE_LETTER_AND_NUMBER_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");

    public final static Pattern ONLY_LETTER_AND_NUMBER_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");

    public final static Pattern ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_PATTERN = Pattern.compile("^([a-zA-Z]|[a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9])$");

    public final static String PAMIRS = "pamirs";

    public final static String SPRING_PLACEHOLDER = "\\$\\{([a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9])\\}";

    public final static String C_PLACEHOLDER = "%s";

    public final static Pattern ONLY_SINGLE_LETTER_PATTERN_PATTERN = Pattern.compile("^[a-zA-Z]");

    public final static Pattern ONLY_LETTER_AND_NUMBER_AND_COMMA_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*[a-zA-Z0-9]$");

    public final static Pattern ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_AND_POINT_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_.]*[a-zA-Z0-9]$");

    public final static Pattern ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_AND_COMMA_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9]$");

    public final static Pattern ONLY_LETTER_AND_NUMBER_AND_POINT_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9.]*[a-zA-Z0-9]$");

    public final static Pattern ONLY_NUMBER_AND_COMMA_PATTERN = Pattern.compile("^[0-9][0-9,]*[0-9]$");

    public final static Pattern ONLY_SINGLE_NUMBER_PATTERN = Pattern.compile("^[0-9][0-9]$");

    public final static Pattern MUST_LETTER_AND_NUMBER_AND_POINT_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");

    public final static Pattern NO_CHINESE_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");

    /**
     * 校验：必须小写字母，且不超过8位字符
     */
    public static Boolean onlyLowercaseLetter(String string) {
        return ONLY_LOWERCASE_LETTER_PATTERN.matcher(string).matches();
    }

    /**
     * 校验：必须字母开头，且只能有字母数字,比如字段名称
     */
    public static Boolean onlyLowercaseLetterAndNumber(String string) {
        return ONLY_LOWERCASE_LETTER_AND_NUMBER_PATTERN.matcher(string).matches();
    }

    /**
     * 校验：必须字母开头，且只能有字母数字,比如字段名称
     */
    public static Boolean onlyLetterAndNumber(String string) {
        return ONLY_LETTER_AND_NUMBER_PATTERN.matcher(string).matches();
    }

    /**
     * 校验：必须字母开头，且只能有字母数字下划线,比如tableName,column
     */
    public static Boolean onlyLetterAndNumberAndUnderline(String string) {
        return ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_PATTERN.matcher(string).matches();
    }

    /**
     * 校验：必须字母开头，且只能有字母数字下划线,比如tableName,column
     */
    public static Boolean onlyLetterAndNumberAndUnderlineAndPlaceholder(String string) {
        String tempString = string
                .replace(SPRING_PLACEHOLDER, PAMIRS)
                .replace(C_PLACEHOLDER, PAMIRS);
        return onlyLetterAndNumberAndUnderline(tempString);
    }


    /**
     * 校验：逗号分割字段名
     * 必须字母开头，且只能有字母数字,比如字段名称合集
     */
    public static Boolean onlyLetterAndNumberAndComma(String string) {

        if (string.length() == 1) return ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(string).matches();
        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            if (split[i].length() == 1) {
                boolean match1 = ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(split[i]).matches();
                if (!match1) return false;
            } else {
                boolean match2 = ONLY_LETTER_AND_NUMBER_AND_COMMA_PATTERN.matcher(split[i]).matches();
                if (!match2) return false;
            }
        }
        return true;
    }

    /**
     * 校验：逗号分割字段名
     * 必须字母开头，且只能有字母数字下划线,比如字段名称合集
     */
    public static Boolean onlyLetterAndNumberAndUnderlineAndComma(String string) {

        if (string.length() == 1) return ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(string).matches();
        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            if (split[i].length() == 1) {
                boolean match1 = ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(split[i]).matches();
                if (!match1) return false;
            } else {
                boolean match2 = ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_AND_COMMA_PATTERN.matcher(split[i]).matches();
                if (!match2) return false;
            }
        }
        return true;
    }

    /**
     * 校验：必须字母开头，且只能有字母数字点，且不能点结尾，modelModel，moduleName
     * 并且被点分割的也必须字母开头
     */
    public static Boolean onlyLetterAndNumberAndPoint(String string) {

        if (string.length() == 1) return ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(string).matches();
        boolean isRight = ONLY_LETTER_AND_NUMBER_AND_POINT_PATTERN.matcher(string).matches();
        if (!isRight) return false;
        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        for (String s : split) {
            if (s.length() == 1) {
                boolean match1 = ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(s).matches();
                if (!match1) return false;
            } else {
                boolean match2 = ONLY_LETTER_AND_NUMBER_AND_POINT_PATTERN.matcher(s).matches();
                if (!match2) return false;
            }
        }
        return true;
    }

    /**
     * 校验：必须字母开头，且只能有字母数字点下划线，且不能点结尾，modelModel，moduleName
     * 并且被点分割的也必须字母开头，且分割后最后一段不能有下划线
     */
    public static Boolean onlyLetterAndNumberAndPointAndUnderline(String string) {

        if (string.length() == 1) return ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(string).matches();
        boolean isRight = ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_AND_POINT_PATTERN.matcher(string).matches();
        if (!isRight) return false;
        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        int i = 1;
        for (String s : split) {
            if (s.length() == 1) {
                boolean match1 = ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(s).matches();
                if (!match1) return false;
            } else if (i == split.length) {
                boolean match2 = ONLY_LETTER_AND_NUMBER_PATTERN.matcher(s).matches();
                if (!match2) return false;
            } else {
                boolean match3 = ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_PATTERN.matcher(s).matches();
                if (!match3) return false;
            }
            i++;
        }
        return true;
    }

    /**
     * 校验：List<Long>必须数字开头，仅含数字、英文逗号（协议），且不能逗号结尾,不能连续多个逗号
     */
    @SuppressWarnings("unused")
    public static Boolean onlyNumberAndComma(String string) {
        if (string.length() == 1) return ONLY_SINGLE_NUMBER_PATTERN.matcher(string).matches();
        boolean isRight = ONLY_NUMBER_AND_COMMA_PATTERN.matcher(string).matches();
        if (!isRight) return false;
        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            String s = split[i];
            if (split[i].length() == 1) {
                boolean match1 = ONLY_SINGLE_NUMBER_PATTERN.matcher(s).matches();
                if (!match1) return false;
            } else {
                boolean match = ONLY_NUMBER_AND_COMMA_PATTERN.matcher(s).matches();
                if (!match) return false;
            }
        }
        return true;
    }

    /**
     * 校验：不能出现中文的字段
     */

    @SuppressWarnings("unused")
    public static Boolean noChinese(String string) {
        return NO_CHINESE_PATTERN.matcher(string).matches();
    }

    /**
     * 校验：fieldName.fieldName的形式，比如related字段
     */

    @SuppressWarnings("unused")
    public static Boolean mustLetterAndNumberAndPoint(String string) {
        if (!string.contains(".")) {
            return false;
        }
        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        for (String s : split) {
            boolean match = MUST_LETTER_AND_NUMBER_AND_POINT_PATTERN.matcher(s).matches();
            if (!match) return false;
        }
        return true;
    }

    /**
     * 校验：pro.shushi.pamirs.Model_l4的形式，比如全限定类名
     */

    @SuppressWarnings("unused")
    public static Boolean mustLetterAndNumberAndPointAndUnderline(String string) {
        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        for (String s : split) {
            boolean match = ONLY_LETTER_AND_NUMBER_AND_UNDERLINE_PATTERN.matcher(s).matches();
            if (!match) return false;
        }
        return true;
    }

    /**
     * 校验：逗号分割，分割出的小块只能点分割
     */
    @SuppressWarnings("unused")
    public static Boolean onlyLetterAndNumberAndCommaAndPoint(String string) {
//        String onlySingleLetterPattern1 = "^[a-zA-Z][a-zA-Z0-9\\.]*[a-zA-Z0-9]";

        if (string.length() == 1) return ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(string).matches();
//        String pattern = "^[a-zA-Z][a-zA-Z0-9]*[a-zA-Z0-9]$";

        String[] split = string.split(CharacterConstants.SEPARATOR_ESCAPE_COMMA);
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            if (split[i].length() == 1) {
                boolean match1 = ONLY_SINGLE_LETTER_PATTERN_PATTERN.matcher(split[i]).matches();
                if (!match1) return false;
            }
            if (split[i].contains(CharacterConstants.SEPARATOR_DOT)) {
                String[] split1 = split[i].split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
                for (String s : split1) {
                    Boolean aBoolean = onlyLowercaseLetterAndNumber(s);
                    if (!aBoolean) return false;
                }
            } else {
                Boolean aBoolean = onlyLowercaseLetterAndNumber(split[i]);
                if (!aBoolean) return false;
            }
        }
        return true;
    }


}
