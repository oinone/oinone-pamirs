package pro.shushi.pamirs.connectors.event.rocketmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.api.NotifySendCallback;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;

@Component
public class TestSendCallback implements NotifySendCallback {

    private static final Logger logger = LoggerFactory.getLogger(TestSendCallback.class);

    @Override
    public void callback(NotifySendResult notifySendResult) {
//        TestModel testModel = (TestModel) notifySendResult.getMsg();
//        if (notifySendResult.isSuccess())
//            logger.info("第{}次发送成功", testModel.getTestInteger());
//        else
//            logger.info("第{}次发送失败", testModel.getTestInteger());
    }
}
