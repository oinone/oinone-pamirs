package pro.shushi.pamirs.framework.connectors.event.kafka.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import pro.shushi.pamirs.framework.common.init.PamirsInit;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyProducer;
import pro.shushi.pamirs.framework.connectors.event.common.PamirsEventProperties;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.engine.EventEngine;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyType;
import pro.shushi.pamirs.framework.connectors.event.kafka.KafkaNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.kafka.KafkaNotifyProducer;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyAppCtxAware;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.manager.NotifyListenerWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.KAFKA_APPCTXAWARE_BEAN_NAME;
import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.KAFKA_NOTIFY_PRODUCER_BEAN_NAME;
import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.KAFKA_TEMPLATE_BEAN_NAME;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_BIZ;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_BIZ_KEY;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_LOGGER;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_LOGGER_KEY;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_SYSTEM;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_SYSTEM_KEY;

/**
 * KafkaAppCtxAware
 *
 * @author yakir on 2023/12/13 10:05.
 */
@Slf4j
@Configuration(value = KAFKA_APPCTXAWARE_BEAN_NAME)
@DependsOn({PamirsInit.BEAN_NAME, KAFKA_TEMPLATE_BEAN_NAME})
@ConditionalOnProperty(prefix = "spring.kafka", value = "bootstrap-servers", matchIfMissing = false)
@Conditional(NotifySwitchCondition.class)
public class KafkaAppCtxAware extends AbstractNotifyAppCtxAware {

    private final KafkaProperties kafkaProperties;

    public KafkaAppCtxAware(Environment environment, KafkaProperties kafkaProperties) {
        super(environment);
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Order(Integer.MAX_VALUE - 1000)
    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) {
        NotifyProducer<KafkaTemplate<String, Object>> notifyProducer = BeanDefinitionUtils.getBean(KafkaNotifyProducer.class);
        Map<String, String> notifyMap = Optional.ofNullable(applicationContext.getBean(PamirsEventProperties.class))
                .map(PamirsEventProperties::getNotifyMap)
                .orElse(Collections.emptyMap());
        for (Map.Entry<String, String> entry : notifyMap.entrySet()) {
            if (StringUtils.equalsIgnoreCase(entry.getValue(), NotifyType.KAFKA.name())) {
                switch (entry.getKey()) {
                    case EVENT_SYS_SYSTEM:
                        EventEngine.register(EVENT_SYS_SYSTEM_KEY, notifyProducer);
                        break;
                    case EVENT_SYS_BIZ:
                        EventEngine.register(EVENT_SYS_BIZ_KEY, notifyProducer);
                        break;
                    case EVENT_SYS_LOGGER:
                        EventEngine.register(EVENT_SYS_LOGGER_KEY, notifyProducer);
                        break;
                }
            }
        }
        registerNotifyListener();
    }

    @Override
    public AbstractNotifyListener<?> notifyListener(NotifyListenerWrapper listenerWrapper, NotifyConsumer<? extends Serializable> consumer) {
        return new KafkaNotifyListener<>(listenerWrapper, consumer, kafkaProperties);
    }

    @Override
    public AbstractNotifyListener<?> notifyDeprecatedListener(NotifyListenerWrapper listenerWrapper, NotifyEventListener eventListener) {
        return new KafkaNotifyListener<>(listenerWrapper, eventListener, kafkaProperties);
    }

    @Bean(name = KAFKA_NOTIFY_PRODUCER_BEAN_NAME)
    @DependsOn({KAFKA_APPCTXAWARE_BEAN_NAME, KAFKA_TEMPLATE_BEAN_NAME})
    @ConditionalOnMissingBean
    public KafkaNotifyProducer kafkaNotifyProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaNotifyProducer(kafkaTemplate);
    }

    @PreDestroy
    public void destroy() {
        super._destroy();
    }
}
