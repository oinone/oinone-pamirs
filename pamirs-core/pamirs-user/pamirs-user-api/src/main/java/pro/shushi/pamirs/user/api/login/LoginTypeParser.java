package pro.shushi.pamirs.user.api.login;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * @author shier
 * @author wangxian
 * date 2020/4/16
 */
public class LoginTypeParser {

    // https://blog.csdn.net/I_am_hardy/article/details/123947516
    // 前端传参是首字母大写的，到HttpServlet就变成小写了。
    // http协议header字段名大小写不敏感的，所以每个框架的写法可能都不一样，如果需要判断请求头相等需要都考虑，做忽略大小写判断
    public static String getLoginType() {
        String loginType = PamirsSession.getRequestVariables().getHeader("loginType");
        return StringUtils.isBlank(loginType) ? "COOKIE" : loginType;
    }
}
