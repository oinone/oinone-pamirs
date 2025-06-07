package pro.shushi.pamirs.core.common.entry;

/**
 * 键值对实例对象
 *
 * @author Adamancy Zhang on 2021-02-20 19:00
 */
public class NameValueEntity {

    /**
     * 键
     */
    private String key;

    /**
     * 值
     */
    private String value;

    public NameValueEntity() {
    }

    public NameValueEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
