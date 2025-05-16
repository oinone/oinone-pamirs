package pro.shushi.pamirs.trigger.notify.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumeBefore;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;

import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Conditional(NotifySwitchCondition.class)
public class NotifyConsumeBeforeImpl implements NotifyConsumeBefore {

    @Override
    public void consumeBefore(NotifyEvent notifyEvent) {
        //设置消费上下文
        String sessionMapJSON = notifyEvent.getProperty(NotifyConstant.PAMIRS_SESSION_KEY);
        if (StringUtils.isNotBlank(sessionMapJSON)) {
            Map<String, String> sessionMap = JSON.parseObject(sessionMapJSON, new TypeReference<Map<String, String>>() {}.getType());
            if (sessionMap != null) {
                PamirsSession.fillSessionFromMap(sessionMap);
            }
        }
    }
}
