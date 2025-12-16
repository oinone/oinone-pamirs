package pro.shushi.pamirs.sso.client.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import pro.shushi.pamirs.sso.client.config.PamirsSsoProperties;

public class PamirsSsoInterceptor implements HandlerInterceptor {

    private final PamirsSsoProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public PamirsSsoInterceptor(PamirsSsoProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        String requestURI = request.getRequestURI();

        // 1. 排除路径放行
        for (String exclude : properties.getExcludeUrls()) {
            if (pathMatcher.match(exclude, requestURI)) {
                return true;
            }
        }

        // 2. 判断是否需要拦截
        boolean shouldIntercept = properties.getUrlPatterns().isEmpty();
        if (!shouldIntercept) {
            for (String pattern : properties.getUrlPatterns()) {
                if (pathMatcher.match(pattern, requestURI)) {
                    shouldIntercept = true;
                    break;
                }
            }
        }

        if (!shouldIntercept) {
            return true;
        }

        // 3. 检查 token（简化版）
        String token = request.getParameter("sso_token");
        if (token != null && !token.trim().isEmpty()) {
            // TODO: 调用 SSO Server 验证 token
            return true;
        }

        // 4. 重定向到 SSO 登录
        String currentUrl = getCurrentFullUrl(request);
        String encodedReturnUrl = URLEncoder.encode(currentUrl, StandardCharsets.UTF_8.toString());
        String loginUrl = properties.getServerUrl()
                + "?client_id=" + properties.getClientId()
                + "&redirect_uri=" + encodedReturnUrl;

        response.sendRedirect(loginUrl);
        return false;
    }

    private String getCurrentFullUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }
}