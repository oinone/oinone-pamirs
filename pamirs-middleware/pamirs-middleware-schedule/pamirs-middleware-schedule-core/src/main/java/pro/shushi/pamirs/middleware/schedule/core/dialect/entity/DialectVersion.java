package pro.shushi.pamirs.middleware.schedule.core.dialect.entity;

/**
 * 方言版本
 *
 * @author Adamancy Zhang at 21:35 on 2023-06-27
 */
public class DialectVersion {

    private final String type;

    private final String version;

    private final String majorVersion;

    public DialectVersion(String type, String version, String majorVersion) {
        this.type = type;
        this.version = version;
        this.majorVersion = majorVersion;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getMajorVersion() {
        return majorVersion;
    }
}
