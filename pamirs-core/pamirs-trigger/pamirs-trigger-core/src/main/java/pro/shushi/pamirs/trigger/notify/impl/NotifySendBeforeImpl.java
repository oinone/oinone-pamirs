package pro.shushi.pamirs.trigger.notify.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.api.NotifySendBefore;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;

import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Conditional(NotifySwitchCondition.class)
public class NotifySendBeforeImpl implements NotifySendBefore {

    @Override
    public void sendBefore(NotifyEvent notifyEvent) {
        Map<String, String> sessionMap = PamirsSession.fetchSessionMap();
        if (MapUtils.isNotEmpty(sessionMap)) {
            //设置消息发送上下文
            notifyEvent.putProperty(NotifyConstant.PAMIRS_SESSION_KEY, JSON.toJSONString(sessionMap));
        }
    }
}
