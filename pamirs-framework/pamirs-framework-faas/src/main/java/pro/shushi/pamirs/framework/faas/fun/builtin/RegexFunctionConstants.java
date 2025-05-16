package pro.shushi.pamirs.framework.faas.fun.builtin;

/**
 * @author shier
 * date  2021/4/6 2:31 下午
 */
public interface RegexFunctionConstants {

    String CHECK_PHONE = "^(1[3-9])\\d{9}$";

    /**
     * 邮箱校验
     */
    String CHECK_EMAIL = "^[a-z0-9A-Z]+[-|a-z0-9A-Z._]*@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";

    /**
     * 强密码校验
     * 校验的规则：8-16位的包括大小写英文和特殊符号
     */
    String CHECK_PWD = "^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9~!@&%#_(.)]{8,16}$";

    /**
     * 整数校验
     * 校验的规则：所有的整数
     */
    String CHECK_INTEGER = "^-{0,1}[0-9]\\d*$?";

    /**
     * 身份证的校验
     * 校验的规则：中国的身份证校验，简单校验，只校验位数，可以是15位或者是18位的身份证
     */
    String CHECK_ID_CARD = "^\\(d{15}$|(^\\d{18}$)|(^\\d{17}(\\d|X|x))$";

    /**
     * URL校验
     * 校验的规则：http/https协议的校验，支持IP和PORT，也支持域名格式的校验
     */
    String CHECK_URL = "^(?:(?:https?)://)(?:(?:1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])(?:\\.(?:1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)){2}(?:\\.(?:1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*)(?::([1-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]))?(?:/\\S*)?$";

    /**
     * 中文校验
     * 校验的规则：只有中文的校验
     */
    String CHECK_CHINESE = "^[\\u4e00-\\u9fa5]{1,}$";

    /**
     * 纯数字校验
     */
    String CHECK_NUMBER = "^[0-9]*$";

    /**
     * 两位小数校验
     * 包括正数和负数
     */
    String CHECK_TWO_DIG = "^-{0,1}[0-9]+(\\.[0-9]{2})?$";

    /**
     * IP校验（ipv4和ipv6）
     */
    String CHECK_IP = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";

    /**
     * 中文校验
     */
    String CHECK_CONTAINS_CHINESE = "^.?[\\u4e00-\\u9fa5]{0,}.?$";

    /**
     * 字符校验：只能由英文、数字、下划线组成
     */
    String CHECK_CODE = "^[a-z0-9A-Z_]*$";

    /**
     * 字符校验：只能包含英文和数字
     */
    String CHECK_ENG_NUM = "^[a-z0-9A-Z]*$";

    /**
     * 字符长度校验：只能输入n个字符
     */
    String CHECK_SIZE = "^.{%s}$";

    /**
     * 字符长度校验：最少输入n个字符
     */
    String CHECK_SIZE_MIN = "^.{%s,}$";

    /**
     * 字符长度校验：最大输入n个字符
     */
    String CHECK_SIZE_MAX = "^.{0,%s}$";

    /**
     * 字符长度校验：字符的范围是[n,m]
     */
    String CHECK_SIZE_RANGE = "^.{%s,%s}$";

}
