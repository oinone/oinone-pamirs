package pro.shushi.pamirs.trigger.notify;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.trigger.config.DefaultListenerSwitchCondition;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;

import java.util.HashMap;

/**
 * 该消费通知监听用于处理所有通过 {@link NotifyHook}发出的固定Topic的消息
 *
 * @author Adamancy Zhang at 00:00 on 2020-04-22
 */
@Slf4j
@Component
@Conditional({NotifySwitchCondition.class, DefaultListenerSwitchCondition.class})
@NotifyListener(topic = NotifyConstant.DEFAULT_NOTIFY_EVENT_LISTENER_TOPIC, tags = "*")
public class DefaultNotifyConsumer implements NotifyConsumer<HashMap<String, Object>> {

    @Override
    public void consume(Message<HashMap<String, Object>> event) {
        MessageHeaders headers = event.getHeaders();
        String originFunctionNamespace = headers.get(NotifyConstant.FUNCTION_NAMESPACE_KEY, String.class);
        String originFunctionFun = headers.get(NotifyConstant.FUNCTION_FUN_KEY, String.class);
        String targetFunctionNamespace = headers.get(NotifyConstant.TARGET_FUNCTION_NAMESPACE_KEY, String.class);
        String targetFunctionFun = headers.get(NotifyConstant.TARGET_FUNCTION_FUN_KEY, String.class);
        if (StringUtils.isNotBlank(targetFunctionNamespace) && StringUtils.isNotBlank(targetFunctionFun)) {
            Function function = PamirsSession.getContext().getFunction(targetFunctionNamespace, targetFunctionFun);
            if (function != null && function.getArguments().size() == 1) {
                Fun.run(targetFunctionNamespace, targetFunctionFun, event.getPayload());
                return;
            }
        }
        log.error("消费异常 originFunctionNamespace: {} originFunctionFun: {} targetFunctionNamespace: {} targetFunctionFun: {}", originFunctionNamespace, originFunctionFun, targetFunctionNamespace, targetFunctionFun);
    }
}
