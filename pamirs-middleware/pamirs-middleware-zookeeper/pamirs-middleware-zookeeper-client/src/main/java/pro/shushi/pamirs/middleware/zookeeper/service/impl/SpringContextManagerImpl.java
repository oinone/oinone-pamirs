package pro.shushi.pamirs.middleware.zookeeper.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.middleware.zookeeper.service.SpringContextManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class SpringContextManagerImpl implements SpringContextManager {

    @Autowired
    private ApplicationContext context;

    @Override
    public ApplicationContext getContext() {
        return context;
    }

    @Override
    public <T> T getBean(Class<T> cls) {
        List<T> beanList = getBeansOfTypeByOrdered(cls);
        if (!beanList.isEmpty()) {
            return beanList.get(0);
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> cls, String beanName) {
        return context.getBeansOfType(cls).get(beanName);
    }

    @Override
    public Object getBean(String beanName) {
        if (context.containsBean(beanName)) {
            return context.getBean(beanName);
        }
        return null;
    }

    @Override
    public <T> List<T> getBeansOfTypeByOrdered(Class<T> cls) {
        Map<String, T> beanMap = context.getBeansOfType(cls);
        List<T> beanList = new ArrayList<>(beanMap.values());
        AnnotationAwareOrderComparator.sort(beanList);
        return beanList;
    }

    @Override
    public <T> T registerBean(Class<T> cls, String beanName) {
        return registerBean(cls, beanName, null);
    }

    @Override
    public <T> T registerBean(Class<T> cls, String beanName, Consumer<BeanDefinitionBuilder> consumer) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
        if (consumer != null) {
            consumer.accept(builder);
        }
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
        return getBean(cls, beanName);
    }

    @Override
    public <T> T registerSingletonBean(Class<T> cls, String beanName, Object singletonInstance) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(beanName, singletonInstance);
        return getBean(cls, beanName);
    }
}
