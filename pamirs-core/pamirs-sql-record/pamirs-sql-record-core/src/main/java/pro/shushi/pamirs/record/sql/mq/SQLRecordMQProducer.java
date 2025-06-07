package pro.shushi.pamirs.record.sql.mq;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.engine.EventEngine;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.canal.domain.Row;

import java.util.List;

/**
 * SQLRecordMQProducer
 *
 * @author yakir on 2023/06/29 19:42.
 */
@Slf4j
@Component(SQLRecordMQProducer.BEAN_NAME)
public class SQLRecordMQProducer {

    public static final String BEAN_NAME = "sqlRecordProducer";

    public boolean send(String topic, List<Row> messages) {
        if (null == messages || messages.isEmpty()) {
            log.debug("消息列表数据为空");
            return true;
        }

        try {
            for (Row msg : messages) {
                NotifySendResult sendResult = EventEngine.systemNotifyProducer()
                        .sendOrderly(topic, msg.getEventType().name(), msg, String.valueOf(msg.getId()));
                if (!sendResult.isSuccess()) {
                    log.error("SQLRecord 发送事件失败 {} 原因", sendResult.getNotifyResult(), sendResult.getThrowable());
                    throw new RuntimeException(sendResult.getThrowable());
                }
            }
            return true;
        } catch (Throwable ex) {
            log.error("Send Msg Error", ex);
            return false;
        }
    }
}
