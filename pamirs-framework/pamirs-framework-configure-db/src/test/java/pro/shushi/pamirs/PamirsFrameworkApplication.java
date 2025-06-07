package pro.shushi.pamirs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SpringBootApplication(
        scanBasePackages = {"pro.shushi.pamirs"},
        exclude = {DataSourceAutoConfiguration.class}
)
class PamirsFrameworkApplication {

    public static void main(String[] args) {

        SpringApplication.run(PamirsFrameworkApplication.class, args);

    }

}
