package pro.shushi.pamirs.framework.common.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import pro.shushi.pamirs.meta.api.core.configure.yaml.SpringTypeConverterRegister;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SqlInjector bean转换器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 12:56 上午
 */
@SuppressWarnings("unused")
@Configuration
public class SpringTypeConvertersConfiguration {

    @Bean(name = "conversionService")
    @DependsOn(BeanDefinitionUtils.beanName)
    public ConversionService conversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        Set<Converter<?, ?>> converterSet = new HashSet<>();
        Map<String, SpringTypeConverterRegister> beanMap = BeanDefinitionUtils.getBeansOfType(SpringTypeConverterRegister.class);
        for (SpringTypeConverterRegister register : beanMap.values()) {
            converterSet.add(register.register());
        }
        bean.setConverters(converterSet); //add converters
        bean.afterPropertiesSet();
        return bean.getObject();
    }

}
