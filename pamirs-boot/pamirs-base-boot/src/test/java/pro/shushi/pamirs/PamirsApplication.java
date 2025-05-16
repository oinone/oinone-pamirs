package pro.shushi.pamirs;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import pro.shushi.pamirs.framework.connectors.data.kv.RedisClusterConfig;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SpringBootApplication(
        scanBasePackages = {"pro.shushi.pamirs"},
        exclude= {DataSourceAutoConfiguration.class}
        )
@MapperScan(value = "pro.shushi.pamirs", annotationClass = Mapper.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, RedisClusterConfig.class
}))
class PamirsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PamirsApplication.class, args);

    }

}
