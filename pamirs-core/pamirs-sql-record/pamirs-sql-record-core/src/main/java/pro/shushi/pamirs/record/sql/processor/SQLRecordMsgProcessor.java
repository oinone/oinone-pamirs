package pro.shushi.pamirs.record.sql.processor;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.middleware.canal.domain.Row;
import pro.shushi.pamirs.record.sql.manager.RecordMessageParser;
import pro.shushi.pamirs.record.sql.manager.SQLRecordManager;
import pro.shushi.pamirs.record.sql.mq.SQLRecordMQProducer;
import pro.shushi.pamirs.record.sql.pojo.ReadResult;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SQLRecordMsgProcessor
 *
 * @author yakir on 2023/06/29 20:17.
 */
@Slf4j
public class SQLRecordMsgProcessor<SRM extends SQLRecordManager> implements Runnable {

    private final SQLRecordMQProducer sqlRecordMQProducer;
    private final SRM sqlRecordManager;

    private static final TypeReference<SQLRecord> TR = new TypeReference<SQLRecord>() {};

    public SQLRecordMsgProcessor(SQLRecordMQProducer sqlRecordMQProducer, SRM sqlRecordManager) {
        this.sqlRecordMQProducer = sqlRecordMQProducer;
        this.sqlRecordManager = sqlRecordManager;
    }

    @Override
    public void run() {
        int failTimes = 0;
        String techName = sqlRecordManager.techName();
        while (true) {
            try {
                if (failTimes >= 10) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException ignored) {
                    }
                }
                ReadResult readResult = sqlRecordManager.readLine();
                String line = readResult.getLine();
                if (null == line || line.isEmpty()) {
                    failTimes++;
                    continue;
                }

                failTimes = 0;

                SQLRecord data = null;
                try {
                    data = JsonUtils.parseObject(line, TR);
                } catch (Throwable e) {
                    log.error(techName + " Parsing exception: [{}]", line, e);
                    failTimes = 10;
                    continue;
                }

                String tenant = data.getT();
                if (StringUtils.isNotBlank(tenant)) {
                    PamirsTenantSession.setTenant(tenant);
                }

                String topic = sqlRecordManager.topic();

                if (null == topic) {
                    continue;
                }
                log.info("Topic: [{}]", topic);

                Row row = RecordMessageParser.parser(data);
                List<Row> messages = new ArrayList<>(1);
                messages.add(row);

                boolean sendOk = sqlRecordMQProducer.send(topic, messages);
                if (sendOk) {
                    boolean commitRt = sqlRecordManager.commit(readResult.getPosition());
                    if (!commitRt) {
                        log.warn(techName + " commit pos failed...");
                    }
                } else {
                    log.warn(techName + " Message sending failed, retrying...");
                    TimeUnit.MILLISECONDS.sleep(500L);
                }
            } catch (Throwable throwable) {
                log.error(techName + " SQL Record Msg exception occurred", throwable);
            }
        }
    }
}
