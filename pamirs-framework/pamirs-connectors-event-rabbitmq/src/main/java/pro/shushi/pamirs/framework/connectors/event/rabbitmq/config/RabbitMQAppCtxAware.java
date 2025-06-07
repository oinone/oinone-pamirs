package pro.shushi.pamirs.framework.connectors.event.rabbitmq.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import pro.shushi.pamirs.framework.common.init.PamirsInit;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyProducer;
import pro.shushi.pamirs.framework.connectors.event.common.PamirsEventProperties;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.engine.EventEngine;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyType;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyAppCtxAware;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.manager.NotifyListenerWrapper;
import pro.shushi.pamirs.framework.connectors.event.rabbitmq.RabbitMQNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.rabbitmq.RabbitMQNotifyProducer;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.*;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.*;

/**
 * RabbitMQAppCtxAware
 *
 * @author yakir on 2023/12/13 10:26.
 */
@Slf4j
@Configuration(value = RABBITMQ_APPCTXAWARE_BEAN_NAME)
@DependsOn({PamirsInit.BEAN_NAME})
@ConditionalOnProperty(prefix = "spring.rabbitmq", value = "host", matchIfMissing = false)
@Conditional(NotifySwitchCondition.class)
public class RabbitMQAppCtxAware extends AbstractNotifyAppCtxAware {

    private final RabbitProperties rabbitProperties;

    public RabbitMQAppCtxAware(Environment environment, RabbitProperties rabbitProperties) {
        super(environment);
        this.rabbitProperties = rabbitProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Order(Integer.MAX_VALUE - 1000)
    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) {
        NotifyProducer<RabbitMessagingTemplate> notifyProducer = applicationContext.getBean(RabbitMQNotifyProducer.class);
        Map<String, String> notifyMap = Optional.ofNullable(applicationContext.getBean(PamirsEventProperties.class))
                .map(PamirsEventProperties::getNotifyMap)
                .orElse(Collections.emptyMap());
        for (Map.Entry<String, String> entry : notifyMap.entrySet()) {
            if (StringUtils.equalsIgnoreCase(entry.getValue(), NotifyType.RABBITMQ.name())) {
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

    @Bean(name = RABBITMQ_NOTIFY_PRODUCER_BEAN_NAME)
    @DependsOn({RABBITMQ_APPCTXAWARE_BEAN_NAME, RABBITMQ_TEMPLATE_BEAN_NAME})
    @ConditionalOnMissingBean
    public RabbitMQNotifyProducer kafkaNotifyProducer(RabbitMessagingTemplate rabbitMessagingTemplate) {
        return new RabbitMQNotifyProducer(rabbitMessagingTemplate);
    }

    @Bean
    @DependsOn({RABBITMQ_APPCTXAWARE_BEAN_NAME, RABBITMQ_TEMPLATE_BEAN_NAME})
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        return new RabbitAdmin(rabbitTemplate);
    }

    @Override
    public AbstractNotifyListener<?> notifyListener(NotifyListenerWrapper listenerWrapper, NotifyConsumer<? extends Serializable> consumer) {
        return new RabbitMQNotifyListener(listenerWrapper, consumer, rabbitProperties);
    }

    @Override
    public AbstractNotifyListener<?> notifyDeprecatedListener(NotifyListenerWrapper listenerWrapper, NotifyEventListener eventListener) {
        return new RabbitMQNotifyListener(listenerWrapper, eventListener, rabbitProperties);
    }

    @Override
    protected void destroy() {
        super._destroy();
    }

}
