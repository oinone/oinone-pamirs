package pro.shushi.pamirs.user.api.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.check.UserInfoChecker;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.spi.UserPatternCheckApi;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;

@Slf4j
public class UserPatternCheckUtils {

    public static void checkPassword(String password, Boolean allowBlank) {
        if (checkBlank(password, allowBlank)) return;
        if (!UserInfoChecker.checkPassword(password)) {
            log.info("{}，密码:{}", USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR.msg(), password);
            throw PamirsException.construct(USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR).errThrow();
        }
    }

    public static void checkEmail(String email, Boolean allowBlank) {
        if (checkBlank(email, allowBlank)) return;
        if (!UserInfoChecker.checkEmail(email)) {
            log.info("{}，email:{}", USER_PARAM_EMAIL_ERROR.msg(), email);
            throw PamirsException.construct(USER_PARAM_EMAIL_ERROR).errThrow();
        }
    }

    public static void checkLogin(String login, Boolean allowBlank) {
        if (checkBlank(login, allowBlank)) return;
        if (!UserInfoChecker.checkLogin(login)) {
            log.info("{}，login:{}", USER_PARAM_LOGIN_ERROR.msg(), login);
            throw PamirsException.construct(USER_PARAM_LOGIN_ERROR).errThrow();
        }
    }

    public static void checkPhone(String phone, Boolean allowBlank) {
        if (checkBlank(phone, allowBlank)) return;
        if (!UserInfoChecker.checkPhone(phone)) {
            log.info("{}，phone:{}", USER_PARAM_PHONE_ERROR.msg(), phone);
            throw PamirsException.construct(USER_PARAM_PHONE_ERROR).errThrow();
        }
    }

    public static void checkName(String name, Boolean allowBlank) {
        if (checkBlank(name, allowBlank)) return;
        if (!UserInfoChecker.checkName(name)) {
            log.info("{}，name:{}", USER_PARAM_NAME_ERROR.msg(), name);
            throw PamirsException.construct(USER_PARAM_NAME_ERROR).errThrow();
        }
    }

    public static void checkNickName(String nickname, Boolean allowBlank) {
        if (checkBlank(nickname, allowBlank)) return;
        if (!UserInfoChecker.checkNickname(nickname)) {
            log.info("{}，用户nickname是{}", USER_PARAM_NICKNAME_ERROR.msg(), nickname);
            throw PamirsException.construct(USER_PARAM_NICKNAME_ERROR).errThrow();
        }
    }

    public static void checkRealName(String realname, Boolean allowBlank) {
        if (checkBlank(realname, allowBlank)) return;
        if (!UserInfoChecker.checkRealname(realname)) {
            log.info("{}，realname:{}", USER_PARAM_REALNAME_ERROR.msg(), realname);
            throw PamirsException.construct(USER_PARAM_REALNAME_ERROR).errThrow();
        }
    }

    private static boolean checkBlank(String param, Boolean allowBlank) {
        if (StringUtils.isBlank(param)) {
            if (BooleanUtils.isTrue(allowBlank)) {
                return true;
            }
            throw PamirsException.construct(USER_PARAM_EMPTY_ERROR).errThrow();
        }
        return false;
    }

    public static void checkIdCard(String idCard, Boolean allowBlank) {
        if (checkBlank(idCard, allowBlank)) return;
        if (!isIDNumber(idCard)) {
            log.info("{}，idCard:{}", USER_PARAM_IDCARD_ERROR.msg(), idCard);
            throw PamirsException.construct(USER_PARAM_IDCARD_ERROR).errThrow();
        }
    }

    @Deprecated
    public static void userPatternCheck(PamirsUser pamirsUser) {
        Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension().userPatternCheck(pamirsUser);
    }

    /**
     * 身份证号码验证
     */
    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾
        boolean matches = IDNumber.matches(regularExpression);
        //判断第18位校验值
        if (matches) {
            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return matches;
    }

}