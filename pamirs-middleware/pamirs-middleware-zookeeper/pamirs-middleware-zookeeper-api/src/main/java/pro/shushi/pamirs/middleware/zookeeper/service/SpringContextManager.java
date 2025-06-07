package pro.shushi.pamirs.middleware.zookeeper.service;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.function.Consumer;


public interface SpringContextManager {

    ApplicationContext getContext();

    <T> T getBean(Class<T> cls);

    <T> T getBean(Class<T> cls, String beanName);

    Object getBean(String beanName);

    <T> List<T> getBeansOfTypeByOrdered(Class<T> cls);

    <T> T registerBean(Class<T> cls, String beanName);

    <T> T registerBean(Class<T> cls, String beanName, Consumer<BeanDefinitionBuilder> consumer);

    <T> T registerSingletonBean(Class<T> cls, String beanName, Object singletonInstance);
}
