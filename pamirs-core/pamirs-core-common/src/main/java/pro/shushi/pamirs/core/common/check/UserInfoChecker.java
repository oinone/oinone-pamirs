package pro.shushi.pamirs.core.common.check;

import pro.shushi.pamirs.framework.faas.fun.builtin.RegexFunctionConstants;

import java.util.regex.Pattern;

/**
 * @author zbh
 * @date 2021/9/184:16 下午
 */
public class UserInfoChecker {

    private static final String CHECK_PWD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()\\[\\]{}<>?\\\\+])[A-Za-z\\d~!@#$%^&*()\\[\\]{}<>?\\\\+]{8,32}$"; //必须包含数字、大写字母、小写字母、特殊符号，8-32位
    private static final String CHECK_LOGIN  = "^[a-z0-9A-Z_.@-]{1,35}$";
    private static final String CHECK_NAME = "^[a-zA-Z\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5]{1,31}$";
    private static final String CHECK_REALNAME = "^[a-zA-Z\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5]{1,31}$";
    private static final String CHECK_NICKNAME = "^[a-zA-Z\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5]{1,31}$";
    private static final String CHECK_PHONE = "^(1[3456789])\\d{9}$";  //手机号：数字1开头，第二位数字范围是3～9,一共是11位的数字.

    private static final Pattern phoneChecker = Pattern.compile(CHECK_PHONE);
    private static final Pattern passWordChecker = Pattern.compile(CHECK_PWD); // 至少一个小写字母一个大写字母 8-32 位 特殊字符： ~!@#$%^&*()[]{}<>?\+

    private static final Pattern LoginChecker = Pattern.compile(CHECK_LOGIN); // 用户名开头：英文开头，仅支持英文和数字。2-32个字。

    private static final Pattern NameChecker     = Pattern.compile(CHECK_NAME); //// 用户名开头：英文开头，仅支持英文和数字。2-32个字。 以后要弃用login,改用name
    private static final Pattern RealnameChecker = Pattern.compile(CHECK_REALNAME); //1. 2-32个字 2. 仅支持中文、英文和数字，且必须以中文或英文开头 3. 必填
    private static final Pattern NicknameChecker = Pattern.compile(CHECK_NICKNAME); //1. 2-32个字 2. 仅支持中文、英文和数字，且必须以中文或英文开头 3. 必填


    private static final Pattern emailChecker = Pattern.compile(RegexFunctionConstants.CHECK_EMAIL); // 邮箱以数字或者字母开头

    public static boolean checkPhone(String phone){
        return phoneChecker.matcher(phone).matches();
    }

    public static boolean checkName(String name){
        return NameChecker.matcher(name).matches();
    }

    public static boolean checkNickname(String nickname){
        return NicknameChecker.matcher(nickname).matches();
    }
    public static boolean checkRealname(String realname){
        return RealnameChecker.matcher(realname).matches();
    }

    public static boolean checkPassword(String password){
        return passWordChecker.matcher(password).matches();
    }

    public static boolean checkEmail(String email){
        return emailChecker.matcher(email).matches();
    }
    public static boolean checkLogin(String login){
        return LoginChecker.matcher(login).matches();
    }
}
