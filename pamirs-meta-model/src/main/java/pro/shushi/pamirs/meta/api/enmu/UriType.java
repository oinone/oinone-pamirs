package pro.shushi.pamirs.meta.api.enmu;

/**
 * Uri请求类型类型
 */
public enum UriType {

    SCHEDULE("SCHEDULE"),
    DUBBO("DUBBO"),
    NOTIFY("NOTIFY"),
    HTTP("HTTP");

    private String type;

    public String getType() {
        return type;
    }

    UriType(String value) {
        this.type = value;
    }

}
