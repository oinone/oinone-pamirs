package pro.shushi.pamirs.middleware.schedule.deployer.controller;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        IM_OK = true;
    }
}
