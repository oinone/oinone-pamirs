package pro.shushi.pamirs.eip.api.context;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Adamancy Zhang
 * @date 2020-11-05 18:16
 */
@Slf4j
public class EipCamelContext {

    private final ModelCamelContext camelContext;

    private final AtomicBoolean isEnabled = new AtomicBoolean(false);

    private ProducerTemplate producerTemplate;

    private final AtomicBoolean isProducerEnabled = new AtomicBoolean(false);

    private EipCamelContext(ModelCamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public ModelCamelContext getCamelContext() {
        return camelContext;
    }

    public Boolean getIsEnabled() {
        return isEnabled.get() && isProducerEnabled.get();
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public void start() throws Exception {
        if (!isEnabled.getAndSet(true)) {
            try {
                camelContext.start();
            } catch (Exception e) {
                isEnabled.set(false);
                throw e;
            }
        }
        if (!isProducerEnabled.getAndSet(true)) {
            if (producerTemplate == null) {
                producerTemplate = camelContext.createProducerTemplate();
            }
            try {
                producerTemplate.start();
            } catch (Exception e) {
                isProducerEnabled.set(false);
                throw e;
            }
        }
    }

    public void stop() {
        if (isProducerEnabled.getAndSet(false)) {
            try {
                producerTemplate.stop();
            } catch (Exception e) {
                isProducerEnabled.set(true);
                throw e;
            }
        }
        if (isEnabled.getAndSet(false)) {
            try {
                camelContext.stop();
            } catch (Exception e) {
                isEnabled.set(true);
                throw e;
            }
        }
    }

    private static EipCamelContext CONTEXT = null;

    public static EipCamelContext getContext() {
        if (CONTEXT == null) {
            synchronized (EipCamelContext.class) {
                if (CONTEXT == null) {
                    CONTEXT = createContext();
                }
            }
        }
        return CONTEXT;
    }

    private static EipCamelContext createContext() {
        ModelCamelContext camelContext = new DefaultCamelContext();
        return new EipCamelContext(camelContext);
    }
}
