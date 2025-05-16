//package pro.shushi.pamirs.trigger.convert;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.stereotype.Component;
//import pro.shushi.pamirs.framework.connectors.event.annotation.Notify;
//import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
//import pro.shushi.pamirs.framework.connectors.event.api.NotifyDeprecatedConsumer;
//import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
//import pro.shushi.pamirs.framework.connectors.event.config.EventConfiguration;
//import pro.shushi.pamirs.framework.connectors.event.context.EventEngine;
//import pro.shushi.pamirs.framework.connectors.event.rocketmq.RocketMQEventPushConsumer;
//import pro.shushi.pamirs.framework.connectors.event.rocketmq.config.RocketMQNotifyConfiguration;
//import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
//import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
//import pro.shushi.pamirs.meta.api.dto.common.Message;
//import pro.shushi.pamirs.meta.api.dto.common.Result;
//import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
//import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
//import pro.shushi.pamirs.meta.common.constants.ModelModelConstants;
//import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
//import pro.shushi.pamirs.meta.common.util.UUIDUtil;
//import pro.shushi.pamirs.trigger.model.NotifyDefinition;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//@Slf4j
//@Component
//public class NotifyConverter implements ModelConverter<NotifyDefinition, Method> {
//
////    @Autowired
////    private FunctionConverter functionConverter;
//
//    @Autowired
//    private EventConfiguration notifyConfiguration;
//
//    @Autowired
//    private RocketMQNotifyConfiguration rocketMQNotifyConfiguration;
//
//    @Override
//    public int priority() {
//        return 2;
//    }
//
//    @Override
//    public Result validate(ExecuteContext context, MetaNames names, Method source) {
//        Result result = new Result();
//        if (!notifyConfiguration.getEnabled())
//            return result;
//        Notify notifyAnnotation = AnnotationUtils.getAnnotation(source, Notify.class);
//        NotifyListener notifyListenerAnnotation = AnnotationUtils.getAnnotation(source, NotifyListener.class);
//        if (notifyAnnotation == null && notifyListenerAnnotation == null)
//            return result.error();
//        if (notifyAnnotation != null && notifyListenerAnnotation != null) {
//            context.broken();
//            return result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
//                    .setMessage("不允许同时使用Notify和NotifyListener注解"));
//        }
//        if (rocketMQNotifyConfiguration.getEnabled()) {
//            if (notifyAnnotation != null) {
////                Result functionVerifyResult = functionConverter.validate(context, names, source);
////                if (!functionVerifyResult.getSuccess()) {
////                    context.broken();
////                    result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
////                            .setMessage("注册消息生产者时，该方法必须被指定为系统函数"));
////                }
//                verifyProducerMethod(result, notifyAnnotation, source);
//            }
//            if (notifyListenerAnnotation != null) {
//                verifyConsumerMethod(result, notifyListenerAnnotation, source);
//                if (result.isSuccess())
//                    return result;
//            }
//            if (!result.isSuccess()) {
//                context.broken();
//            }
//        }
//        return result.error();
//    }
//
//    @Override
//    public NotifyDefinition convert(MetaNames names, Method source, NotifyDefinition metaModelObject) {
//        Notify notifyAnnotation = AnnotationUtils.getAnnotation(source, Notify.class);
//        NotifyListener notifyListenerAnnotation = AnnotationUtils.getAnnotation(source, NotifyListener.class);
//        if (rocketMQNotifyConfiguration.getEnabled()) {
//            if (notifyAnnotation != null) {
//                metaModelObject.setNamesrvAddr(rocketMQNotifyConfiguration.getNamesrvAddr())
//                        .setClientIP(rocketMQNotifyConfiguration.getClientIp())
//                        .setInstanceName(rocketMQNotifyConfiguration.getInstanceName())
//                        .setNamespace(rocketMQNotifyConfiguration.getNamespace());
//            }
//            if (notifyListenerAnnotation != null) {
//                metaModelObject.setNamesrvAddr(rocketMQNotifyConfiguration.getNamesrvAddr())
//                        .setClientIP(rocketMQNotifyConfiguration.getClientIp())
//                        .setInstanceName(rocketMQNotifyConfiguration.getInstanceName())
//                        .setNamespace(rocketMQNotifyConfiguration.getNamespace());
//                String consumerGroup = (source.getDeclaringClass().getName() + "." + source.getName()).replaceAll("\\.", "_");
//                NotifyDeprecatedConsumer consumer = EventEngine.registerConsumer(consumerGroup, () -> RocketMQEventPushConsumer.newInstance(rocketMQNotifyConfiguration.getNamesrvAddr(), consumerGroup)
//                        .setClientIP(rocketMQNotifyConfiguration.getClientIp())
//                        .setInstanceName(rocketMQNotifyConfiguration.getInstanceName())
//                        .setNamespace(rocketMQNotifyConfiguration.getNamespace())
//                        .build());
//                if (consumer != null) {
//                    Object returnObject;
//                    try {
//                        source.setAccessible(true);
//                        returnObject = source.invoke(source.getDeclaringClass().newInstance());
//                    } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
//                        throw new RuntimeException("无法实例化通知监听方法", e);
//                    }
//                    consumer.subscribe(notifyListenerAnnotation.topic(), notifyListenerAnnotation.tags());
//                    consumer.registerListener((NotifyConsumer) returnObject);
//                    log.info("注册消息消费者成功 consumerGroup: {}, topic: {}, tags: {}", consumerGroup, notifyListenerAnnotation.topic(), notifyListenerAnnotation.tags());
//                }
//            }
//        }
//        return metaModelObject;
//    }
//
//    @Override
//    public String group() {
//        return ModelModelConstants.event;
//    }
//
//    @Override
//    public String sign(MetaNames names, Method source) {
//        return UUIDUtil.getUUIDNumberString();
//    }
//
//    private void verifyProducerMethod(Result result, Notify notifyAnnotation, Method method) {
//        if (StringUtils.isBlank(notifyAnnotation.topic())) {
//            result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
//                    .setMessage("注册消息生产者时，必须指定消息主题"));
//            return;
//        }
//        if (StringUtils.isBlank(notifyAnnotation.tags())) {
//            result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
//                    .setMessage("注册消息生产者时，必须指定消息标签"));
//            return;
//        }
//    }
//
//    private void verifyConsumerMethod(Result result, NotifyListener notifyListenerAnnotation, Method method) {
//        if (StringUtils.isBlank(notifyListenerAnnotation.topic())) {
//            result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
//                    .setMessage("注册消息消费者时，必须指定订阅主题"));
//            return;
//        }
//        if (StringUtils.isBlank(notifyListenerAnnotation.tags())) {
//            result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
//                    .setMessage("注册消息消费者时，必须指定订阅标签"));
//            return;
//        }
//        if (!NotifyConsumer.class.isAssignableFrom(method.getReturnType())) {
//            result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
//                    .setMessage("注册消息消费者时，必须返回NotifyEventListener接口"));
//            return;
//        }
//        if (method.getParameterCount() != 0) {
//            result.error().addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
//                    .setMessage("注册消息消费者时，必须没有传入参数声明"));
//            return;
//        }
//    }
//}