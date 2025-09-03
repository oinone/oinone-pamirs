package pro.shushi.pamirs.framework.gateways.graph.java.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Data
@ConditionalOnProperty(prefix = "pamirs.security.csrf", name = "check", havingValue = "true")
@Configuration
@WebFilter(filterName = "csrfRefererFilter", urlPatterns = "/*", asyncSupported = true)
public class CsrfRefererFilter implements Filter {

    public static final Logger logger = LoggerFactory.getLogger(CsrfRefererFilter.class);

    /**
     * 过滤器配置对象
     */
    private FilterConfig filterConfig = null;

    /**
     * 是否启用
     */
    @Value("${pamirs.security.csrf.check:false}")
    private boolean check;

    /**
     * 忽略的URL
     */
    @Value("${pamirs.security.csrf.allowedReferer:}")
    private String allowedReferer;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // 不启用的情况
        if (!check) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String referer = httpRequest.getHeader("Referer");
        if (!isAllowedRefererUrl(referer)) {
            log.warn("请求referer:" + referer + "，不匹配allowedReferer，请求拒绝(403)");
            // 返回 403 Forbidden 错误
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 继续处理请求
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * 判断是否为允许的URL
     *
     * @param referer
     * @return
     */
    private boolean isAllowedRefererUrl(String referer) {
        if (referer == null || StringUtils.isBlank(allowedReferer)) {
            return false;
        }

        List<String> allowedReferers = Arrays.asList(allowedReferer.trim().split(","));
        for (String alReferer : allowedReferers) {
            if (referer.startsWith(alReferer)) {
                return true;
            }
        }

        return false;
    }

}