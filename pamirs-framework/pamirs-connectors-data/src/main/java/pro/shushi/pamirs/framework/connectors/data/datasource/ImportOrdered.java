package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.MybatisPlusAutoConfiguration;

/**
 * 用于强制定义类扫描顺序
 *
 * @author Adamancy Zhang at 20:11 on 2025-05-14
 */
@Configuration
@Import({
        PamirsDataSourceBeanConfiguration.class,
        MybatisPlusAutoConfiguration.class
})
public class ImportOrdered {
}
