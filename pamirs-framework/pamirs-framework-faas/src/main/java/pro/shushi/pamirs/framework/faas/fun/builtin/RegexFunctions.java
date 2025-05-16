package pro.shushi.pamirs.framework.faas.fun.builtin;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;

import java.util.regex.Pattern;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.REGEX;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 正则函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(NamespaceConstants.expression)
public class RegexFunctions implements RegexFunctionConstants {

    @Function.Advanced(
            displayName = "正则匹配", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("MATCHES")
    @Function(name = "MATCHES", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MATCHES(text,regex)\n" +
                    "函数说明: 校验字符串是否满足正则匹配，例如regex为[a-zA-Z][a-zA-Z0-9]*$，来校验text是否匹配"
    )
    public static Boolean matches(String text, String regex) {
        if (null == text || null == regex) {
            return Boolean.FALSE;
        }
        return Pattern.matches(regex, text);
    }

    @Function.Advanced(
            displayName = "校验手机号", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_PHONE")
    @Function(name = "CHECK_PHONE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_PHONE(text)\n" +
                    "函数说明: 校验是否符合手机号的规则"
    )
    public static Boolean checkPhone(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_PHONE, text);
    }

    @Function.Advanced(
            displayName = "校验邮箱的格式", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_EMAIL")
    @Function(name = "CHECK_EMAIL", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_EMAIL(text)\n" +
                    "函数说明: 校验是否符合邮箱的规则"
    )
    public static Boolean checkEmail(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_EMAIL, text);
    }

    @Function.Advanced(
            displayName = "校验用户名", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_USER_NAME")
    @Function(name = "CHECK_USER_NAME", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_USER_NAME(text)\n" +
                    "函数说明: 校验是否符合用户名的规则"
    )
    public static Boolean checkUserName(String text) {
        return null != text && !text.isEmpty();
    }

    @Function.Advanced(
            displayName = "强密码校验", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_PWD")
    @Function(name = "CHECK_PWD", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_PWD(text)\n" +
                    "函数说明: 校验是否符合强密码的规则"
    )
    public static Boolean checkPwd(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_PWD, text);
    }

    @Function.Advanced(
            displayName = "校验整数格式", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_INTEGER")
    @Function(name = "CHECK_INTEGER", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_INTEGER(text)\n" +
                    "函数说明: 校验是否符合整数的规则"
    )
    public static Boolean checkInteger(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_INTEGER, text);
    }

    @Function.Advanced(
            displayName = "校验中国身份证格式", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_ID_CARD")
    @Function(name = "CHECK_ID_CARD", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_ID_CARD(text)\n" +
                    "函数说明: 校验是否符合中国身份证的规则"
    )
    public static Boolean checkIdCard(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_ID_CARD, text);
    }

    @Function.Advanced(
            displayName = "校验URL格式", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_URL")
    @Function(name = "CHECK_URL", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_URL(text)\n" +
                    "函数说明: 校验是否符合URL的规则"
    )
    public static Boolean checkUrl(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_URL, text);
    }

    @Function.Advanced(
            displayName = "校验是否是中文格式", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_CHINESE")
    @Function(name = "CHECK_CHINESE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_CHINESE(text)\n" +
                    "函数说明: 校验是否符合中文的规则"
    )
    public static Boolean checkChinese(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_CHINESE, text);
    }

    @Function.Advanced(
            displayName = "校验数字格式", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_NUMBER")
    @Function(name = "CHECK_NUMBER", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_NUMBER(text)\n" +
                    "函数说明: 校验是否符合是数字的规则"
    )
    public static Boolean checkNumber(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_NUMBER, text);
    }

    @Function.Advanced(
            displayName = "验证是否两位小数", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_TWO_DIG")
    @Function(name = "CHECK_TWO_DIG", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_TWO_DIG(text)\n" +
                    "函数说明: 验证是否两位小数"
    )
    public static Boolean checkTwoDig(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_TWO_DIG, text);
    }

    @Function.Advanced(
            displayName = "IP地址校验", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_IP")
    @Function(name = "CHECK_IP", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_IP(text)\n" +
                    "函数说明: IP地址校验"
    )
    public static Boolean checkIP(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_IP, text);
    }

    @Function.Advanced(
            displayName = "是否包含中文校验", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_CONTAINS_CHINESE")
    @Function(name = "CHECK_CONTAINS_CHINESE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_CONTAINS_CHINESE(text)\n" +
                    "函数说明: 校验是否包含中文的规则"
    )
    public static Boolean checkContainsChinese(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_CONTAINS_CHINESE, text);
    }

    @Function.Advanced(
            displayName = "校验编码", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_CODE")
    @Function(name = "CHECK_CODE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_CODE(text)\n" +
                    "函数说明: 只能由英文、数字、下划线组成"
    )
    public static Boolean checkCode(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_CODE, text);
    }

    @Function.Advanced(
            displayName = "校验格式", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_ENG_NUM")
    @Function(name = "CHECK_ENG_NUM", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_ENG_NUM(text)\n" +
                    "函数说明: 只能包含英文和数字"
    )
    public static Boolean checkEngNumber(String text) {
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        return Pattern.matches(CHECK_ENG_NUM, text);
    }

    @Function.Advanced(
            displayName = "校验字符范围", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_SIZE")
    @Function(name = "CHECK_SIZE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_SIZE(text)\n" +
                    "函数说明: 输入m个字符"
    )
    public static Boolean checkSize(String text, Integer size) {
        if (null == size) {
            return Boolean.FALSE;
        }
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        String regex = String.format(CHECK_SIZE, size);
        return Pattern.matches(regex, text);
    }

    @Function.Advanced(
            displayName = "校验字符范围", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_MIN_SIZE")
    @Function(name = "CHECK_MIN_SIZE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_MIN_SIZE(text)\n" +
                    "函数说明: 至少输入m个字符"
    )
    public static Boolean checkMinSize(String text, Integer min) {
        if (null == min) {
            return Boolean.FALSE;
        }
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        String regex = String.format(CHECK_SIZE_MIN, min);
        return Pattern.matches(regex, text);
    }

    @Function.Advanced(
            displayName = "校验字符范围", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_MAX_SIZE")
    @Function(name = "CHECK_MAX_SIZE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_MAX_SIZE(text)\n" +
                    "函数说明: 至多输入m个字符"
    )
    public static Boolean checkMaxSize(String text, Integer max) {
        if (null == max) {
            return Boolean.FALSE;
        }
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        String regex = String.format(CHECK_SIZE_MAX, max);
        return Pattern.matches(regex, text);
    }

    @Function.Advanced(
            displayName = "校验字符范围", language = JAVA,
            builtin = true, category = REGEX
    )
    @Function.fun("CHECK_SIZE_RANGE")
    @Function(name = "CHECK_SIZE_RANGE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CHECK_SIZE_RANGE(text)\n" +
                    "函数说明: 至多输入m个字符"
    )
    public static Boolean checkSizeRange(String text, Integer min, Integer max) {
        if (null == min || null == max) {
            return Boolean.FALSE;
        }
        if (null == text) {
            text = StringUtils.EMPTY;
        }
        String regex = String.format(CHECK_SIZE_RANGE, min, max);
        return Pattern.matches(regex, text);
    }
}
