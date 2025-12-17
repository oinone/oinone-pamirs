package pro.shushi.pamirs;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan(value = "pro.shushi.pamirs", annotationClass = Mapper.class)
public class PamirsBaseApplication {

    public static void main(String[] args) {

//        System.setProperty(CLIENT_LOG_USESLF4J, "true");

        SpringApplication.run(PamirsBaseApplication.class, args);

    }

}
