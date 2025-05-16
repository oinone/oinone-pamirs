package pro.shushi.pamirs.connectors.event.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "pro.shushi.pamirs.framework.common",
        "pro.shushi.pamirs.framework.connectors.event",
        "pro.shushi.pamirs.framework.connectors.event.rocketmq"
})
public class EventApplication {

    public static void main(String[] args) {

        SpringApplication.run(EventApplication.class, args);
    }
}
