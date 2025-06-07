package pro.shushi.pamirs.middleware.schedule.deployer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * ScheduleApp
 *
 * @author yakir on 2020/06/11 15:17.
 */
@MapperScan(basePackages = {"pro.shushi.pamirs.middleware.schedule.core.dao"}, annotationClass = Mapper.class)
@SpringBootApplication(scanBasePackages = {
        "pro.shushi.pamirs.middleware.common",
        "pro.shushi.pamirs.middleware.zookeeper",
        "pro.shushi.pamirs.middleware.schedule"
})
@EnableDubbo(scanBasePackages = "pro.shushi.pamirs.middleware.schedule.core.service")
public class ScheduleApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ScheduleApp.class)
                .web(WebApplicationType.SERVLET)
                .run(args);

    }
}
