package pro.shushi.pamirs.framework.connectors.event.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.annotation.Notify;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyProducer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyQueueSelector;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTagsGenerator;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.engine.EventEngine;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyBizType;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.framework.connectors.event.enumeration.EventExpEnum.SYSTEM_ERROR;

/**
 * Notify注解AOP
 *
 * @author Adamancy Zhang at 12:00 on 2020-04-22
 */
@Slf4j
@Aspect
@Component
@Order
@Conditional(NotifySwitchCondition.class)
public class NotifyAspect {

    private final Map<String, NotifyTagsGenerator> tagsGeneratorCache = new ConcurrentHashMap<>();
    private final Map<String, NotifyQueueSelector> queueSelectorCache = new ConcurrentHashMap<>();

    @Pointcut("@annotation(pro.shushi.pamirs.framework.connectors.event.annotation.Notify)")
    protected void doWorker() {
    }

    @AfterReturning(value = "doWorker()", returning = "obj")
    private void doAfterReturning(JoinPoint joinPoint, Object obj) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Notify notify = method.getAnnotation(Notify.class);
        NotifyProducer producer = null;
        String bizType = notify.notifyBizType();
        if (NotifyBizType.biz.name().equalsIgnoreCase(bizType)) {
            producer = EventEngine.bizNotifyProducer();
        } else if (NotifyBizType.logger.name().equalsIgnoreCase(bizType)) {
            producer = EventEngine.loggerNotifyProducer();
        } else if (NotifyBizType.system.name().equalsIgnoreCase(bizType)) {
            producer = EventEngine.systemNotifyProducer();
        } else {
            producer = EventEngine.get(bizType);
        }

        String topic = notify.topic();
        if (null == producer) {
            log.error("指定生产者不存在，消息发送失败 topic: {} tags: {}", topic, notify.tags());
            throw PamirsException.construct(SYSTEM_ERROR)
                    .appendMsg("找不到Producer")
                    .appendMsg(bizType)
                    .errThrow();
        }

        String tag = null;
        if (!NotifyTagsGenerator.class.equals(notify.tagsGenerator()) && NotifyTagsGenerator.class.isAssignableFrom(notify.tagsGenerator())) {
            NotifyTagsGenerator tagsGenerator = tagsGeneratorCache.computeIfAbsent(topic, _topic -> {
                try {
                    return notify.tagsGenerator().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("实例化TagGenerator异常");
                    return null;
                }
            });

            if (null != tagsGenerator) {
                tag = tagsGenerator.tagsGenerator(obj);
                if (StringUtils.equalsIgnoreCase(tag, "null")) {
                    tag = null;
                }
            }
        }

        String hashKey = null;
        if (!NotifyQueueSelector.class.equals(notify.querySelector()) && NotifyQueueSelector.class.isAssignableFrom(notify.querySelector())) {
            NotifyQueueSelector queueSelector = queueSelectorCache.computeIfAbsent(topic, _topic -> {
                try {
                    return notify.querySelector().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("实例化TagGenerator异常");
                    return null;
                }
            });

            if (null != queueSelector) {
                hashKey = queueSelector.hashing(obj);
                if (StringUtils.equalsIgnoreCase(hashKey, "null")) {
                    hashKey = null;
                }
            }
        }

        NotifySendResult notifySendResult;

        if (StringUtils.isNotBlank(hashKey)) {
            notifySendResult = producer.sendOrderly(topic, tag, obj, hashKey);
        } else {
            notifySendResult = producer.send(topic, tag, obj);
        }

        if (notifySendResult.isSuccess()) {
            log.info("消息发送成功 topic: {} tags: {}", topic, notify.tags());
        } else {
            if (notifySendResult.getThrowable() == null) {
                log.error("消息发送失败 topic: {} tags: {}", topic, notify.tags());
            } else {
                log.error("消息发送失败 topic: {} tags: {}", topic, notify.tags(), notifySendResult.getThrowable());
            }
        }
    }
}
