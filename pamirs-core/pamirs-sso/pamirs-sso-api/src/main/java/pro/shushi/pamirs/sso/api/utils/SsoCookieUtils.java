package pro.shushi.pamirs.sso.api.utils;

import org.springframework.core.env.Environment;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SsoCookieUtils {
    public static Cookie get(HttpServletRequest request, String key) {
        Cookie[] arr_cookie = request.getCookies();
        if (arr_cookie != null && arr_cookie.length > 0) {
            for (Cookie cookie : arr_cookie) {
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 删除 SSO 生成的 Cookie
     *
     * @param request
     * @param response
     * @param key
     */
    public static void remove(HttpServletRequest request, HttpServletResponse response, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            set(response, key, "", 0, true);
        }
    }

    private static void set(HttpServletResponse response, String key, String value, int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        Environment environment = BeanDefinitionUtils.getEnvironment();
        if (environment != null) {
            String domain = environment.getProperty("server.servlet.session.cookie.domain");
            if (domain != null) {
                cookie.setDomain(domain);
            }
        }
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        response.addCookie(cookie);
    }
}
