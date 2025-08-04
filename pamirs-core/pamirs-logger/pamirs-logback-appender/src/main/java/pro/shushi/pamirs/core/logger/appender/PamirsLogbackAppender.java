package pro.shushi.pamirs.core.logger.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * PamirsLogbackAppender
 *
 * @author yakir on 2023/12/27 14:14.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class PamirsLogbackAppender<E> extends PamirsLogbackAppenderConfig<E> {

    private static final String KAFKA_LOGGER_PREFIX = KafkaProducer.class.getPackage().getName().replaceFirst("\\.producer$", "");

    private final AppenderAttachableImpl<E> aa = new AppenderAttachableImpl<>();
    private final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<>();
    private final FailedDeliveryCallback<E> failedDeliveryCallback = new FailedDeliveryCallback<E>() {
        @Override
        public void onFailedDelivery(E evt, Throwable throwable) {
            aa.appendLoopOnAppenders(evt);
        }
    };

    private KafkaProducerLazyInit kafkaProducerLazyInit;

    public PamirsLogbackAppender() {
    }

    @Override
    public void doAppend(E e) {
        ensureDeferredAppends();
        if (e instanceof ILoggingEvent && ((ILoggingEvent) e).getLoggerName().startsWith(KAFKA_LOGGER_PREFIX)) {
            deferAppend(e);
        } else {
            super.doAppend(e);
        }
    }

    @Override
    public void start() {
        if (!checkPrerequisites()) {
            return;
        }

        if (partition != null && partition < 0) {
            partition = null;
        }

        addProducerConfigValue(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        if (logEncoder == null) {
            addWarn("未设置日志logEncoder appender name [\"" + name + "\"]. 使用默认" + "[" + StringSerializer.class.getCanonicalName() + "]");
            logEncoder = StringSerializer.class;
        }
        addProducerConfigValue(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, logEncoder);

        this.kafkaProducerLazyInit = new KafkaProducerLazyInit();

        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (kafkaProducerLazyInit != null && kafkaProducerLazyInit.isInitialized()) {
            try {
                kafkaProducerLazyInit.get().close();
            } catch (KafkaException e) {
                this.addWarn("Failed to shut down kafka producer: " + e.getMessage(), e);
            }
            kafkaProducerLazyInit = null;
        }
    }

    @Override
    public void addAppender(Appender<E> newAppender) {
        aa.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return aa.iteratorForAppenders();
    }

    @Override
    public Appender<E> getAppender(String name) {
        return aa.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<E> appender) {
        return aa.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        aa.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<E> appender) {
        return aa.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return aa.detachAppender(name);
    }

    @Override
    protected void append(E e) {
        final String payload = new String(encoder.encode(e), StandardCharsets.UTF_8);
        final String key = keyingStrategy.createKey(e);

        final Long timestamp = getTimestamp(e);

        final ProducerRecord<?, ?> record = new ProducerRecord<>(topic, null, timestamp, key, payload);

        final Producer producer = kafkaProducerLazyInit.get();
        if (producer != null) {
            deliveryStrategy.send(kafkaProducerLazyInit.get(), record, e, failedDeliveryCallback);
        } else {
            failedDeliveryCallback.onFailedDelivery(e, null);
        }
    }

    protected Long getTimestamp(E e) {
        if (e instanceof ILoggingEvent) {
            return ((ILoggingEvent) e).getTimeStamp();
        } else {
            return System.currentTimeMillis();
        }
    }

    private void deferAppend(E event) {
        queue.add(event);
    }

    private void ensureDeferredAppends() {
        E event;

        while ((event = queue.poll()) != null) {
            super.doAppend(event);
        }
    }

    private class KafkaProducerLazyInit {

        private volatile KafkaProducer producer;

        public KafkaProducer get() {
            KafkaProducer result = this.producer;
            if (result == null) {
                synchronized (this) {
                    result = this.producer;
                    if (result == null) {
                        this.producer = result = this.initialize();
                    }
                }
            }

            return result;
        }

        protected KafkaProducer initialize() {
            KafkaProducer producer = null;
            try {
                producer = new KafkaProducer(producerConfig);
            } catch (Exception e) {
                addError("error creating producer", e);
            }
            return producer;
        }

        public boolean isInitialized() {
            return producer != null;
        }
    }

}
