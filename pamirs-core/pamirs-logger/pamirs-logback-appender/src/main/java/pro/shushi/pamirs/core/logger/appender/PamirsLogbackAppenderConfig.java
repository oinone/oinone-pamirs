package pro.shushi.pamirs.core.logger.appender;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.AppenderAttachable;
import pro.shushi.pamirs.core.logger.delivery.AsynchronousDeliveryStrategy;
import pro.shushi.pamirs.core.logger.delivery.DeliveryStrategy;
import pro.shushi.pamirs.core.logger.keying.KeyingStrategy;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

/**
 * PamirsLogbackAppenderConfig
 *
 * @author yakir on 2023/12/27 19:18.
 */
abstract
public class PamirsLogbackAppenderConfig<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {

    protected String topic = null;

    protected Encoder<E> encoder = null;
    protected KeyingStrategy<? super E> keyingStrategy = null;
    protected DeliveryStrategy deliveryStrategy;

    protected Integer partition = null;
    protected String clientJaasConfPath = null;
    protected String kerb5ConfPath = null;

    protected boolean appendTimestamp = true;

    protected Class<?> logEncoder;

    protected Map<String, Object> producerConfig = new HashMap<String, Object>();

    protected boolean checkPrerequisites() {
        boolean errorFree = true;

        if (producerConfig.get(BOOTSTRAP_SERVERS_CONFIG) == null) {
            addError("Kafka 配置异常 \"" + BOOTSTRAP_SERVERS_CONFIG + "\" appender named [\"" + name + "\"].");
            errorFree = false;
        }

        if (topic == null) {
            addError("未设置日志Topic appender name [\"" + name + "\"].");
            errorFree = false;
        }

        if (keyingStrategy == null) {
            addInfo("未设置日志 keyingStrategy appender name [\"" + name + "\"]. 使用默认");
            keyingStrategy = new KeyingStrategy() {};
        }

        if (deliveryStrategy == null) {
            addInfo("未设置日志 deliveryStrategy appender named [\"" + name + "\"]. 使用默认");
            deliveryStrategy = new AsynchronousDeliveryStrategy();
        }

        return errorFree;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setKeyingStrategy(KeyingStrategy<? super E> keyingStrategy) {
        this.keyingStrategy = keyingStrategy;
    }

    public void addProducerConfig(String keyValue) {
        String[] split = keyValue.split("=", 2);
        if (split.length == 2) {
            addProducerConfigValue(split[0], split[1]);
        }
    }

    public void addProducerConfigValue(String key, Object value) {
        this.producerConfig.put(key, value);
    }

    public Map<String, Object> getProducerConfig() {
        return producerConfig;
    }

    public void setDeliveryStrategy(DeliveryStrategy deliveryStrategy) {
        this.deliveryStrategy = deliveryStrategy;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public boolean isAppendTimestamp() {
        return appendTimestamp;
    }

    public void setAppendTimestamp(boolean appendTimestamp) {
        this.appendTimestamp = appendTimestamp;
    }

    public void setClientJaasConfPath(String clientJaasConfPath) {
        this.clientJaasConfPath = clientJaasConfPath;
    }

    public void setKerb5ConfPath(String kerb5ConfPath) {
        this.kerb5ConfPath = kerb5ConfPath;
    }
}
