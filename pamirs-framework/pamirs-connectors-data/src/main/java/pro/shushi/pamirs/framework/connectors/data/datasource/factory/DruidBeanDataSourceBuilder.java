package pro.shushi.pamirs.framework.connectors.data.datasource.factory;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据源生成器，并托管给spring
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/10 6:21 下午
 */
@Component
public class DruidBeanDataSourceBuilder implements DataSourceBuilder {

    private static final String KEY = "com.alibaba.druid.pool.DruidDataSource" + CharacterConstants.SEPARATOR_OCTOTHORPE + Boolean.TRUE;

    private final static String ID = "id";

    private final static String INIT = "init";

    private final static String CLOSE = "close";

    private final static String INIT_CONNECTION_SQLS = "initConnectionSqls";

    @Override
    public DataSource build(String dsKey, Properties properties) {
        properties.remove(DbConstants.FIELD_TYPE);
        String beanName = dsKey + DataSource.class.getSimpleName();
        //  组装bean
        Map<String, String> dataSourceProperties = new HashMap<>();
        for (String property : properties.stringPropertyNames()) {
            dataSourceProperties.put(property, properties.getProperty(property));
        }
        AbstractBeanDefinition beanDefinition = getBeanDefinition(beanName, dataSourceProperties);
        //  注册bean
        DefaultListableBeanFactory beanFactory = BeanDefinitionUtils.getBeanFactory();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return (DataSource) BeanDefinitionUtils.getBean(beanName);
    }

    private AbstractBeanDefinition getBeanDefinition(String beanKey, Map<String, String> properties) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
        builder.getBeanDefinition().setAttribute(ID, beanKey);
        builder.setInitMethodName(INIT);
        builder.setDestroyMethodName(CLOSE);
        List<String> keys = Lists.newArrayList(INIT_CONNECTION_SQLS);
        for (String key : properties.keySet()) {
            if (keys.contains(key)) {
                continue;
            }
            builder.addPropertyValue(key, properties.get(key));
        }
        return builder.getBeanDefinition();
    }

    @Override
    public String key() {
        return KEY;
    }

}
