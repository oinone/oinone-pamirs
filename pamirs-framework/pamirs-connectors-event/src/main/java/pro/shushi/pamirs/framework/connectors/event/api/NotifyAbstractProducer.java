package pro.shushi.pamirs.framework.connectors.event.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NotifyAbstractProducer
 *
 * @author yakir on 2023/12/26 17:43.
 */
abstract
public class NotifyAbstractProducer<TEMPLATE> implements NotifyProducer<TEMPLATE> {

    protected final Map<String, TEMPLATE> templatecMap = new ConcurrentHashMap<>();

    protected final TEMPLATE defaultTemplate;

    protected final AtomicBoolean isOk = new AtomicBoolean(false);

    public NotifyAbstractProducer(TEMPLATE defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    @Override
    public TEMPLATE getDefaultTemplate() {
        return this.defaultTemplate;
    }

    @Override
    public TEMPLATE getTemplate(String topic) {
        return templatecMap.getOrDefault(topic, defaultTemplate);
    }

    @Override
    public void registerTemplate(String topicOrTxGroup, TEMPLATE template) {
        templatecMap.putIfAbsent(topicOrTxGroup, template);
    }


    @Override
    public <P extends NotifyProducer<TEMPLATE>> P isOk() {
        if (isOk.get()) {
            return (P) this;
        } else {
            throw new RuntimeException("Producer为初始化完成");
        }
    }

    @Override
    public void ok() {
        isOk.compareAndSet(false, true);
    }
}
