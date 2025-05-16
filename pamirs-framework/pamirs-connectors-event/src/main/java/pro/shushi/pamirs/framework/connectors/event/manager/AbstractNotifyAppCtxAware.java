package pro.shushi.pamirs.framework.connectors.event.manager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.MethodMetadata;
import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
import pro.shushi.pamirs.framework.connectors.event.annotation.TransactionListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTransactionListener;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractNotifyAppCtxAware
 *
 * @author yakir on 2023/12/13 10:34.
 */
@Slf4j
abstract
public class AbstractNotifyAppCtxAware implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    protected Environment environment;

    protected final Map<String, AbstractNotifyListener<?>> containerMap = new ConcurrentHashMap<>();

    protected AbstractNotifyAppCtxAware(Environment environment) {
        this.environment = environment;
    }

    @SuppressWarnings({"rawtypes"})
    protected void registerNotifyListener() {

        Map<String, NotifyConsumer> beans = this.applicationContext.getBeansOfType(NotifyConsumer.class);

        for (Map.Entry<String, NotifyConsumer> entry : beans.entrySet()) {
            try {
                if (ScopedProxyUtils.isScopedTarget(entry.getKey())) {
                    continue;
                }

                NotifyListenerWrapper listener = null;
                Class<?> klass = AopUtils.getTargetClass(entry.getValue());
                NotifyListener anno = AnnotationUtils.findAnnotation(klass, NotifyListener.class);
                if (null != anno) {
                    listener = NotifyListenerWrapper.fromAnno(anno);
                } else {
                    BeanDefinition beanDefinition = ((GenericApplicationContext) applicationContext).getBeanFactory().getBeanDefinition(entry.getKey());
                    MethodMetadata methodMetadata = (MethodMetadata) beanDefinition.getSource();
                    Map<String, Object> annAttr = methodMetadata.getAnnotationAttributes(NotifyListener.class.getName(), false);
                    listener = NotifyListenerWrapper.fromAttr(annAttr);
                }

                if (StringUtils.isBlank(listener.group())) {
                    listener.group(entry.getKey());
                }

                AbstractNotifyListener<?> notifyListener = notifyListener(listener, (NotifyConsumer<? extends Serializable>) entry.getValue());
                if (null != listener.bodyClass() && !Void.class.equals(listener.bodyClass())) {
                    TopicClassCacheManager.registerType(notifyListener.topic(), listener.bodyClass());
                } else {
                    TopicClassCacheManager.registerType(notifyListener.topic(), entry.getValue());
                }
                containerMap.put(entry.getKey(), notifyListener);
                notifyListener.start();
            } catch (Throwable throwable) {
                log.error("注册消费者异常", throwable);
            }
        }
    }

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    protected void registerDeprecatedNotifyListener() {

        Map<String, NotifyEventListener> beans = this.applicationContext.getBeansOfType(NotifyEventListener.class);

        for (Map.Entry<String, NotifyEventListener> entry : beans.entrySet()) {
            try {
                if (ScopedProxyUtils.isScopedTarget(entry.getKey())) {
                    continue;
                }

                NotifyListenerWrapper listener = null;
                Class<?> klass = AopUtils.getTargetClass(entry.getValue());
                NotifyListener anno = AnnotationUtils.findAnnotation(klass, NotifyListener.class);
                if (null != anno) {
                    listener = NotifyListenerWrapper.fromAnno(anno);
                } else {
                    BeanDefinition beanDefinition = ((GenericApplicationContext) applicationContext).getBeanFactory().getBeanDefinition(entry.getKey());
                    MethodMetadata methodMetadata = (MethodMetadata) beanDefinition.getSource();
                    Map<String, Object> annAttr = methodMetadata.getAnnotationAttributes(NotifyListener.class.getName(), false);
                    listener = NotifyListenerWrapper.fromAttr(annAttr);
                }

                listener.group(entry.getKey());

                AbstractNotifyListener<?> notifyListener = notifyDeprecatedListener(listener, (NotifyEventListener) entry.getValue());
                notifyListener.start();
                containerMap.put(entry.getKey(), notifyListener);
            } catch (Throwable throwable) {
                log.error("注册消费者异常", throwable);
            }
        }
    }

    protected void registerNotifyTxProducer() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(TransactionListener.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            try {
                if (ScopedProxyUtils.isScopedTarget(entry.getKey())) {
                    continue;
                }

                String group = null;
                Class<?> klass = AopUtils.getTargetClass(entry.getValue());
                TransactionListener anno = AnnotationUtils.findAnnotation(klass, TransactionListener.class);
                if (null != anno) {
                    group = anno.value();
                } else {
                    BeanDefinition beanDefinition = ((GenericApplicationContext) applicationContext).getBeanFactory().getBeanDefinition(entry.getKey());
                    MethodMetadata methodMetadata = (MethodMetadata) beanDefinition.getSource();
                    Map<String, Object> annAttr = methodMetadata.getAnnotationAttributes(TransactionListener.class.getName(), false);
                    group = (String) annAttr.get("value");
                }

                Object txListener = entry.getValue();
                if (txListener instanceof NotifyTransactionListener) {
                    notifyTxProducer(group, (NotifyTransactionListener) entry.getValue());
                } else {
                    log.error("Listener[{}]不是[{}]的实例", txListener.getClass(), NotifyTransactionListener.class.getName());
                }
            } catch (Throwable throwable) {
                log.error("注册消费者异常", throwable);
            }
        }
    }

    protected void _destroy() {
        for (Map.Entry<String, AbstractNotifyListener<?>> entry : containerMap.entrySet()) {
            AbstractNotifyListener<?> container = entry.getValue();
            container.destroy();
        }
    }

    protected String applicationName() {
        return environment.getProperty("spring.application.name");
    }

    public void notifyTxProducer(String group, NotifyTransactionListener txListener) {}

    public abstract AbstractNotifyListener<?> notifyListener(NotifyListenerWrapper listenerWrapper, NotifyConsumer<? extends Serializable> consumer);

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    public abstract AbstractNotifyListener<?> notifyDeprecatedListener(NotifyListenerWrapper listenerWrapper, NotifyEventListener eventListener);

    protected abstract void destroy();

}
