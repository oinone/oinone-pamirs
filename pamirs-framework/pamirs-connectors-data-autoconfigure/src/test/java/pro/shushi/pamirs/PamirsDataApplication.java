package pro.shushi.pamirs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SpringBootApplication(
        scanBasePackages = {"pro.shushi.pamirs"},
        exclude= {DataSourceAutoConfiguration.class}
        )
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class
}))
class PamirsDataApplication {

    public static void main(String[] args) {

        SpringApplication.run(PamirsDataApplication.class, args);

    }

}
