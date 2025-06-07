package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
public class ServletRequestHelper {

    public static final String DEFAULT_IP_TEXT = "未知";

    /**
     * 开启Servlet上下文在子线程中可见（需在开启子线程前使用）
     */
    public static void enableServletInheritable() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true);
    }

    /**
     * 禁用Servlet上下文在子线程中可见（需在开启子线程前使用）
     */
    public static void disableServletInheritable() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, false);
    }

    /**
     * <h>获取请求IP地址</h>
     * <p>nginx 反向代理配置（以/pamirs为例）</p>
     * <code>
     * location /pamirs {
     * proxy_pass  http://172.16.124.12:8090;
     * proxy_set_header    Host    $host;
     * proxy_set_header    X-Real-IP   $remote_addr;
     * proxy_set_header    X-Forwarded-For $proxy_add_x_forwarded_for;
     * }
     * </code>
     *
     * @param request {@link HttpServletRequest}
     * @return 客户端真实访问ip
     */
    public static String fetchIP(HttpServletRequest request) {
        try {
            String ipString = request.getHeader("X-Forwarded-For");
            if (StringUtils.isBlank(ipString)) {
                return DEFAULT_IP_TEXT;
            }
            String[] ips = ipString.split(",");
            String ip1 = request.getHeader("Host");//公网主机IP
            String ip2 = request.getHeader("X-Real-IP");//内网IP
            String ip3 = request.getHeader("X-Forwarded-For");//真实访问IP,内网IP
            String ip4 = request.getRemoteHost();
            log.info("请求IP [Host {}] [X-Real-IP {}] [X-Forwarded-For {}] [RemoteHost {}]", ip1, ip2, ip3, ip4);
            if (ips.length >= 2) {
                Collections.reverse(Arrays.asList(ips));
                for (String ip : ips) {
                    ip = ip.trim();
                    if (!ip.equals(ip2)) {
                        return ip;
                    }
                }
            }
            return ips[0].trim();
        } catch (Exception e) {
            return DEFAULT_IP_TEXT;
        }
    }

    public static String fetchUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
