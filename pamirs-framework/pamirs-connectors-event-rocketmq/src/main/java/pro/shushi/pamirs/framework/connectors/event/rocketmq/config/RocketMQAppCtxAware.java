package pro.shushi.pamirs.framework.connectors.event.rocketmq.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import pro.shushi.pamirs.framework.common.init.PamirsInit;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyProducer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTransactionListener;
import pro.shushi.pamirs.framework.connectors.event.common.PamirsEventProperties;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.engine.EventEngine;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyType;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyAppCtxAware;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.manager.NotifyListenerWrapper;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.RocketMQNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.RocketMQNotifyProducer;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.RocketMQProducer;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.RocketMQTemplate;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.marshalling.PamirsMessageJsonConverter;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.util.RocketMQUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.apache.rocketmq.client.log.ClientLogger.CLIENT_LOG_USESLF4J;
import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.*;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.*;

/**
 * RocketMQAppCtxAware
 *
 * @author yakir on 2023/12/09 11:26.
 */
@Slf4j
@Configuration(value = ROCKETMQ_APPCTXAWARE_BEAN_NAME)
@EnableConfigurationProperties(RocketMQProperties.class)
@DependsOn({PamirsInit.BEAN_NAME})
@ConditionalOnClass({MQAdmin.class})
@ConditionalOnProperty(prefix = ROCKETMQ_CONFIG_PREFIX, value = ROCKETMQ_CONFIG_NAME_SRV, matchIfMissing = false)
@Conditional(NotifySwitchCondition.class)
public class RocketMQAppCtxAware extends AbstractNotifyAppCtxAware {

    private final RocketMQProperties rocketMQProperties;

    public RocketMQAppCtxAware(Environment environment, RocketMQProperties rocketMQProperties) {
        super(environment);
        this.rocketMQProperties = rocketMQProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.setProperty(CLIENT_LOG_USESLF4J, Boolean.toString(true));
        String nameServer = rocketMQProperties.getNameServer();
        log.info("RocketMQ NameSrv = {}", nameServer);
        if (nameServer == null) {
            log.error("RocketMQ 配置参数异常 !!!");
        }
        this.applicationContext = applicationContext;
        registerNotifyTxProducer();
    }

    @Order(Integer.MAX_VALUE - 1000)
    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) {
        NotifyProducer<RocketMQTemplate> notifyProducer = BeanDefinitionUtils.getBean(ROCKETMQ_NOTIFY_PRODUCER_BEAN_NAME, RocketMQNotifyProducer.class);
        Map<String, String> notifyMap = Optional.ofNullable(applicationContext.getBean(PamirsEventProperties.class))
                .map(PamirsEventProperties::getNotifyMap)
                .orElse(Collections.emptyMap());
        for (Map.Entry<String, String> entry : notifyMap.entrySet()) {
            if (StringUtils.equalsAnyIgnoreCase(entry.getValue(), NotifyType.ROCKET_MQ.name(), NotifyType.ROCKETMQ.name())) {
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

    @Bean(name = ROCKETMQ_DEFAULT_PRODUCER_BEAN_NAME, destroyMethod = "shutdown")
    @DependsOn(ROCKETMQ_APPCTXAWARE_BEAN_NAME)
    public DefaultMQProducer defaultMQProducer() {
        String nameServer = rocketMQProperties.getNameServer();
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String groupName = Optional.ofNullable(producerConfig)
                .map(RocketMQProperties.Producer::getGroup)
                .filter(StringUtils::isNotBlank)
                .orElse(applicationName());
        Assert.hasText(nameServer, "[spring.rocketmq.name-server] must not be null");
        Assert.hasText(groupName, "[spring.rocketmq.producer.group] must not be null");

        String accessChannel = rocketMQProperties.getAccessChannel();

        String ak = rocketMQProperties.getAccessKey();
        String sk = rocketMQProperties.getSecretKey();
        boolean isEnableMsgTrace = Boolean.TRUE.equals(rocketMQProperties.getProducer().isEnableMsgTrace()) ? rocketMQProperties.getProducer().isEnableMsgTrace() : rocketMQProperties.isEnableMsgTrace();
        String customizedTraceTopic = rocketMQProperties.getProducer().getCustomizedTraceTopic();
        DefaultMQProducer producer = RocketMQUtil.createDefaultMQProducer(groupName, ak, sk, isEnableMsgTrace, customizedTraceTopic);

        producer.setNamesrvAddr(nameServer);
        if (StringUtils.isNotBlank(accessChannel)) {
            producer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        producer.setUseTLS(rocketMQProperties.isTlsEnable());
        if (null != producerConfig) {
            producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
            producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
            producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
            producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
            producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
            producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
            producer.setNamespace(producerConfig.getNamespace());
            producer.setInstanceName(producerConfig.getInstanceName());
        }
        log.info("RocketMQ Producer Group:[{}] NameSrv:[{}]", groupName, nameServer);
        try {
            producer.start();
        } catch (Throwable exp) {
            log.error("启动RocketMQ Producer异常", exp);
        }
        return producer;
    }

    @Bean(name = ROCKETMQ_NOTIFY_PRODUCER_BEAN_NAME, destroyMethod = "destroy")
    @DependsOn({ROCKETMQ_APPCTXAWARE_BEAN_NAME})
    @ConditionalOnMissingBean
    public RocketMQNotifyProducer rocketMQNotifyProducer(PamirsMessageJsonConverter pamirsMessageJsonConverter) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        if (applicationContext.containsBean(ROCKETMQ_DEFAULT_PRODUCER_BEAN_NAME)) {
            rocketMQTemplate.setProducer((DefaultMQProducer) applicationContext.getBean(ROCKETMQ_DEFAULT_PRODUCER_BEAN_NAME));
        }
        rocketMQTemplate.setMessageConverter(pamirsMessageJsonConverter.getMessageConverter());
        RocketMQNotifyProducer rocketMQNotifyProducer = new RocketMQNotifyProducer(rocketMQTemplate);
        rocketMQNotifyProducer.ok();
        return rocketMQNotifyProducer;
    }

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    @Bean(name = ROCKETMQ_PRODUCER_BEAN_NAME, destroyMethod = "destroy")
    @DependsOn({ROCKETMQ_APPCTXAWARE_BEAN_NAME})
    @ConditionalOnMissingBean
    public RocketMQProducer rocketMQProducer(PamirsMessageJsonConverter pamirsMessageJsonConverter) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        if (applicationContext.containsBean(ROCKETMQ_DEFAULT_PRODUCER_BEAN_NAME)) {
            rocketMQTemplate.setProducer((DefaultMQProducer) applicationContext.getBean(ROCKETMQ_DEFAULT_PRODUCER_BEAN_NAME));
        }
        rocketMQTemplate.setMessageConverter(pamirsMessageJsonConverter.getMessageConverter());
        RocketMQProducer rocketMQProducer = new RocketMQProducer(rocketMQTemplate);
        rocketMQProducer.ok();
        return rocketMQProducer;
    }

    @Override
    public void notifyTxProducer(String group, NotifyTransactionListener txListener) {

        String nameServer = rocketMQProperties.getNameServer();
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        Assert.hasText(nameServer, "[spring.rocketmq.name-server] must not be null");
        Assert.hasText(group, "[spring.rocketmq.producer.group] must not be null");

        String accessChannel = rocketMQProperties.getAccessChannel();

        String ak = rocketMQProperties.getAccessKey();
        String sk = rocketMQProperties.getSecretKey();
        boolean isEnableMsgTrace = Boolean.TRUE.equals(rocketMQProperties.getProducer().isEnableMsgTrace()) ? rocketMQProperties.getProducer().isEnableMsgTrace() : rocketMQProperties.isEnableMsgTrace();
        String customizedTraceTopic = rocketMQProperties.getProducer().getCustomizedTraceTopic();

        TransactionMQProducer producer = (TransactionMQProducer) RocketMQUtil.createDefaultMQProducer(group, ak, sk, isEnableMsgTrace, customizedTraceTopic);

        producer.setNamesrvAddr(nameServer);
        if (StringUtils.isNotBlank(accessChannel)) {
            producer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        producer.setUseTLS(rocketMQProperties.isTlsEnable());
        if (null != producerConfig) {
            producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
            producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
            producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
            producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
            producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
            producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
            producer.setNamespace(producerConfig.getNamespace());
            producer.setInstanceName(producerConfig.getInstanceName());
        }

        producer.setTransactionListener(RocketMQUtil.convert(txListener));
        log.info("RocketMQ Producer Group:[{}] NameSrv:[{}]", group, nameServer);

        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setMessageConverter(BeanDefinitionUtils.getBean(PamirsMessageJsonConverter.class).getMessageConverter());
        rocketMQTemplate.setProducer(producer);
        try {
            rocketMQTemplate.afterPropertiesSet();
        } catch (Throwable exp) {
            log.error("启动RocketMQ Tx Producer txGroup:[{}]异常", group, exp);
        }

        BeanDefinitionUtils.getBean(ROCKETMQ_NOTIFY_PRODUCER_BEAN_NAME, RocketMQNotifyProducer.class)
                .registerTemplate(group, rocketMQTemplate);
    }

    @Override
    public AbstractNotifyListener<?> notifyListener(NotifyListenerWrapper listenerWrapper, NotifyConsumer<? extends Serializable> consumer) {
        return new RocketMQNotifyListener(listenerWrapper, consumer, rocketMQProperties);
    }


    @Override
    public AbstractNotifyListener<?> notifyDeprecatedListener(NotifyListenerWrapper listenerWrapper, NotifyEventListener eventListener) {
        return new RocketMQNotifyListener(listenerWrapper, eventListener, rocketMQProperties);
    }

    @PreDestroy
    public void destroy() {
        super._destroy();
    }
}
