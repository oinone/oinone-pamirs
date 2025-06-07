package pro.shushi.pamirs.core.common.pipeline.extension;

import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认交换对象
 *
 * @author Adamancy Zhang on 2021-04-26 17:00
 */
public class DefaultPamirsExchange implements PamirsExchange {

    private Object body;

    private Throwable throwable;

    private final Map<String, Object> properties = new HashMap<>();

    private boolean isInterrupt = false;

    @Override
    public Object getBody() {
        return body;
    }

    @Override
    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public Object getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public Object removeProperty(String key) {
        return properties.remove(key);
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean isInterrupted() {
        return isInterrupt;
    }

    @Override
    public void interrupt() {
        this.isInterrupt = true;
    }
}
