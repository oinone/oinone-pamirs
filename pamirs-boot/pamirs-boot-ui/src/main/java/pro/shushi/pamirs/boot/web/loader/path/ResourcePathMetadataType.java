package pro.shushi.pamirs.boot.web.loader.path;

/**
 * 资源路径元数据类型
 *
 * @author Adamancy Zhang at 20:51 on 2024-01-04
 */
public enum ResourcePathMetadataType {

    VIEW,
    ACTION,
    FIELD;

    public static ResourcePathMetadataType of(String name) {
        for (ResourcePathMetadataType type : ResourcePathMetadataType.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
