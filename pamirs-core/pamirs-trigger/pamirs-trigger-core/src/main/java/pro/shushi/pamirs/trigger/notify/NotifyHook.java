package pro.shushi.pamirs.trigger.notify;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyProducer;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyType;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;
import pro.shushi.pamirs.trigger.model.NotifyDefinition;
import pro.shushi.pamirs.trigger.model.NotifyFunctionDefinition;

//@Component
//@Conditional(NotifySwitchCondition.class)
public class NotifyHook implements HookAfter {

    //    @Hook
    @Override
    public Object run(Function function, Object args) {
        NotifyDefinition notifyDefinition = NotifyContext.getNotifyDefinition(function.getNamespace(), function.getFun());
        if (notifyDefinition != null && notifyDefinition.getActive()) {
            NotifyType notifyType = NotifyType.valueOf(notifyDefinition.getNotifyType().getValue());
            NotifyProducer producer = null;//EventEngine.getProducer(notifyType);
            if (producer != null) {
                NotifyEvent notifyEvent;
                for (NotifyFunctionDefinition targetFunctionDefinition : notifyDefinition.getTargetFunctionList()) {
                    if (StringUtils.isBlank(targetFunctionDefinition.getTopic()) || StringUtils.isBlank(targetFunctionDefinition.getTags())) {
                        notifyEvent = new NotifyEvent(NotifyConstant.DEFAULT_NOTIFY_EVENT_LISTENER_TOPIC, targetFunctionDefinition.getTargetFun(), args);
                    } else {
                        notifyEvent = new NotifyEvent(targetFunctionDefinition.getTopic(), targetFunctionDefinition.getTags(), args);
                    }
                    notifyEvent.putProperty(NotifyConstant.FUNCTION_NAMESPACE_KEY, notifyDefinition.getExecuteNamespace());
                    notifyEvent.putProperty(NotifyConstant.FUNCTION_FUN_KEY, notifyDefinition.getExecuteFun());
                    notifyEvent.putProperty(NotifyConstant.TARGET_FUNCTION_NAMESPACE_KEY, targetFunctionDefinition.getTargetNamespace());
                    notifyEvent.putProperty(NotifyConstant.TARGET_FUNCTION_FUN_KEY, targetFunctionDefinition.getTargetFun());
                    if (notifyDefinition.getTagsGenerator() != null) {
                        notifyEvent.setTagsGenerator(notifyDefinition.getTagsGenerator());
                    }
                    if (notifyDefinition.getSendCallback() != null) {
                        notifyEvent.setSendCallback(notifyDefinition.getSendCallback());
                    }
                    if (notifyDefinition.getQueueSelector() != null) {
                        notifyEvent.setQueueSelector(notifyDefinition.getQueueSelector());
                    }
//                    producer.send(notifyEvent);
                }
            }
        }
        return args;
    }
}
