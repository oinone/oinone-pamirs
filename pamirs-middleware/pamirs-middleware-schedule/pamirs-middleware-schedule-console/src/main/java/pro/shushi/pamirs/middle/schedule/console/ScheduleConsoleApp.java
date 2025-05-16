package pro.shushi.pamirs.middle.schedule.console;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * ScheduleConsoleApp
 *
 * @author yakir on 2020/06/11 15:17.
 */
@SpringBootApplication
public class ScheduleConsoleApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ScheduleConsoleApp.class)
                .web(WebApplicationType.SERVLET)
                .run(args);

    }
}
