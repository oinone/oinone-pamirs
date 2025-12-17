package pro.shushi.pamirs;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import pro.shushi.pamirs.framework.connectors.data.configure.datasource.DataSourceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.datasource.DynamicDataSource;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.sql.DataSource;

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
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class
}))
class PamirsDataApplication {

    public static void main(String[] args) {

//        System.setProperty(CLIENT_LOG_USESLF4J, "true");

        SpringApplication.run(PamirsDataApplication.class, args);

        //noinspection InfiniteLoopStatement
        while (true) {
            DataSourceConfiguration bean = (DataSourceConfiguration) BeanDefinitionUtils.getBean("dataSourceConfiguration");
            System.out.println("size:" + bean.size());
            for (String key : bean.keySet()) {
                System.out.println("ds:" + ((DruidDataSource) BeanDefinitionUtils
                        .getBean(key + DataSource.class.getSimpleName())).getUrl());
                System.out.println("config:" + bean.get(key).get("url"));
            }
            @SuppressWarnings("unused")
            DynamicDataSource dynamicDataSource = BeanDefinitionUtils.getBean(DynamicDataSource.class);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
