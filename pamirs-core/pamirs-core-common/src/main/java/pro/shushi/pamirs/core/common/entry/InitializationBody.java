package pro.shushi.pamirs.core.common.entry;

/**
 * @author Adamancy Zhang
 * @date 2020-11-25 12:54
 */
public class InitializationBody<K, V> {

    private final K key;

    private V value;

    private Object extend;

    private boolean isProcess = false;

    private boolean isReplaceValue = false;

    public InitializationBody(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public InitializationBody<K, V> setValue(V value) {
        this.isReplaceValue = true;
        this.value = value;
        return this;
    }

    public Object getExtend() {
        return extend;
    }

    public InitializationBody<K, V> setExtend(Object extend) {
        this.extend = extend;
        return this;
    }

    public boolean isProcessed() {
        return isProcess;
    }

    public void processed() {
        this.isProcess = true;
    }

    public void reprocess() {
        this.isProcess = false;
    }

    public boolean isReplaceValue() {
        return isReplaceValue;
    }
}