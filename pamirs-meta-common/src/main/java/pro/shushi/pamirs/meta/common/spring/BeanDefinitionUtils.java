package pro.shushi.pamirs.meta.common.spring;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Spring bean 帮助类
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 2:30 上午
 */
@Component
public class BeanDefinitionUtils implements ApplicationContextAware {

    private final static String ID = "id";

    private static ApplicationContext applicationContext;

    public static Object registerBeanDefinition(String beanName, Class clazz, Map<String, Object> properties) {
        AbstractBeanDefinition beanDefinition = getBeanDefinition(beanName, clazz, properties);
        //  注册bean
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return applicationContext.getBean(beanName);
    }

    private static AbstractBeanDefinition getBeanDefinition(String beanName, Class clazz, Map<String, Object> properties) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        builder.getBeanDefinition().setAttribute(ID, beanName);
        if(MapUtils.isNotEmpty(properties)){
            for(String key : properties.keySet()){
                builder.addPropertyValue(key, properties.get(key));
            }
        }
        return builder.getBeanDefinition();
    }

    public static DefaultListableBeanFactory getBeanFactory(){
        return (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static boolean containsBean(String beanName){
        return applicationContext.containsBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz){
        return applicationContext.getBeansOfType(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanDefinitionUtils.applicationContext = applicationContext;
    }

}
