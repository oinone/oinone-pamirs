package pro.shushi.pamirs.record.sql.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.record.sql.manager.*;
import pro.shushi.pamirs.record.sql.mq.SQLRecordMQProducer;
import pro.shushi.pamirs.record.sql.processor.SQLRecordMsgProcessor;
import pro.shushi.pamirs.record.sql.processor.SQLRecordStoreProcessor;

import java.util.concurrent.ExecutorService;

/**
 * SQLRecordProducerInit
 *
 * @author yakir on 2023/06/29 20:06.
 */
@Slf4j
@Component
@Order
@DependsOn({SQLRecordMQProducer.BEAN_NAME, RecordFilterManager.BEAN_NAME})
public class SQLRecordProducerInit implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private SQLRecordMQProducer sqlRecordMQProducer;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        init();
    }

    public void init() {
        SQLRecordChangeDataManager sqlRecordChangeDataManager = SQLRecordChangeDataManager.init();
        SQLRecordBinlogEventManager sqlRecordBinlogEventManager = SQLRecordBinlogEventManager.init();
        ExecutorService exec = SQLRecordAsyncManager.getExecutorService();

        exec.submit(new SQLRecordStoreProcessor<>(sqlRecordBinlogEventManager, () -> SQLRecordQueueManager.get().binlogEventPoll()));
        exec.submit(new SQLRecordStoreProcessor<>(sqlRecordChangeDataManager, () -> SQLRecordQueueManager.get().changeDataPoll()));

        exec.submit(new SQLRecordMsgProcessor<>(sqlRecordMQProducer, sqlRecordBinlogEventManager));
        exec.submit(new SQLRecordMsgProcessor<>(sqlRecordMQProducer, sqlRecordChangeDataManager));

        Runtime.getRuntime()
                .addShutdownHook(new SQLRecordShutdownHook());
    }
}
