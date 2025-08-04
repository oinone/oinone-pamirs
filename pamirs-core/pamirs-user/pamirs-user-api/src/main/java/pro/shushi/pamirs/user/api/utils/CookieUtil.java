package pro.shushi.pamirs.user.api.utils;

import org.springframework.core.env.Environment;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    // 默认缓存时间,单位/秒, 2H
    private static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
    // 保存路径,根路径
    public static final String COOKIE_PATH = "/";

    /**
     * 保存
     *
     * @param response
     * @param key
     * @param value
     */
    public static void set(HttpServletResponse response, String key, String value) {
        set(response, key, value, COOKIE_PATH, -1, true);
    }

    public static Cookie set(String key, String value) {
        return set(key, value, COOKIE_PATH, -1, true);
    }

    /**
     * 保存
     *
     * @param response
     * @param key
     * @param value
     * @param maxAge
     */
    private static void set(HttpServletResponse response, String key, String value, String path, int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        Environment environment = BeanDefinitionUtils.getEnvironment();
        if (environment != null) {
            String domain = environment.getProperty("server.servlet.session.cookie.domain");
            if (domain != null) {
                cookie.setDomain(domain);
            }
        }
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        response.addCookie(cookie);
    }

    private static Cookie set(String key, String value, String path, int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        Environment environment = BeanDefinitionUtils.getEnvironment();
        if (environment != null) {
            String domain = environment.getProperty("server.servlet.session.cookie.domain");
            if (domain != null) {
                cookie.setDomain(domain);
            }
        }
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        return cookie;
    }

    /**
     * 查询value
     *
     * @param request
     * @param key
     * @return
     */
    public static String getValue(HttpServletRequest request, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    /**
     * 查询Cookie
     *
     * @param request
     * @param key
     */
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
     * 删除Cookie
     *
     * @param request
     * @param response
     * @param key
     */
    public static void remove(HttpServletRequest request, HttpServletResponse response, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            set(response, key, "", COOKIE_PATH, 0, true);
        }
    }

}