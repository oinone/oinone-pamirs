package pro.shushi.pamirs.middleware.schedule.deployer.controller;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 健康检查
 *
 * @author Adamancy Zhang on 2021-06-03 09:51
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestController
public class HealthCheckController implements ApplicationListener<ApplicationStartedEvent> {

    private static boolean IM_OK = false;

    private static final String SUCCESS_STRING = "imok";

    private static final String FAILURE_STRING = "no";

    @GetMapping("/ruok")
    public String ruok() {
        if (IM_OK) {
            return SUCCESS_STRING;
        }
        return FAILURE_STRING;
    }

    /**
     * 心跳检查：如Nginx或者SLA健康检查；符合 W3C 定义的规范。
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/health/check")
    public String healthCheck(HttpServletResponse response) throws IOException {
        if (!IM_OK) {
            response.sendError(503);
            response.setStatus(503);
            return FAILURE_STRING;
        }

        return SUCCESS_STRING;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        IM_OK = true;
    }
}
