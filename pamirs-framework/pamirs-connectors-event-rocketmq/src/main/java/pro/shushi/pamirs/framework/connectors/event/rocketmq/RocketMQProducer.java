package pro.shushi.pamirs.framework.connectors.event.rocketmq;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEventSendResult;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;

/**
 * RocketMQProducer
 *
 * @author yakir on 2023/12/28 11:20.
 * @deprecated 即将移除.
 */
@Deprecated
public class RocketMQProducer extends RocketMQNotifyProducer {

    public RocketMQProducer(RocketMQTemplate rocketMQTemplate) {
        super(rocketMQTemplate);
    }

    public NotifyEventSendResult send(NotifyEvent notifyEvent) {

        String group = notifyEvent.getGroup();
        NotifySendResult sendResult = null;
        if (StringUtils.isNotBlank(group)) {
            sendResult = this.sendTx(notifyEvent.getTopic(), notifyEvent.getTags(), group, notifyEvent.getBody(), null);
        } else {
            sendResult = this.send(notifyEvent.getTopic(), notifyEvent.getTags(), notifyEvent.getBody());
        }
        if (sendResult.isSuccess()) {
            return NotifyEventSendResult.ok(notifyEvent);
        } else {
            return NotifyEventSendResult.error(notifyEvent);
        }
    }
}
