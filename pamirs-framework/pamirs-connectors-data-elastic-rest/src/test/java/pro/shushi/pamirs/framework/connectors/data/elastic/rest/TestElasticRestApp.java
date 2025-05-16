package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.configration.ElasticsearchProperty;

/**
 * TestElasticRestApp
 *
 * @author yakir on 2020/04/16 20:09.
 */
@SpringBootApplication(scanBasePackages = {"pro.shushi"})
@EnableConfigurationProperties({
        ElasticsearchProperty.class
})
public class TestElasticRestApp {

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(TestElasticRestApp.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
