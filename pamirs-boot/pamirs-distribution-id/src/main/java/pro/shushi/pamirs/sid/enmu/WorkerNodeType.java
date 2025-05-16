package pro.shushi.pamirs.sid.enmu;

/**
 * WorkerNodeType
 * <li>CONTAINER: Such as Docker
 * <li>ACTUAL: Actual machine
 */
public enum WorkerNodeType {

    CONTAINER("CONTAINER", 1, "容器"),
    ACTUAL("ACTUAL", 2, "实体机");

    /**
     * Lock type
     */
    private final String  key;
    private final Integer value;
    private final String  displayName;


    WorkerNodeType(String key, Integer value, String displayName) {
        this.key = key;
        this.value = value;
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}
