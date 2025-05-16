package pro.shushi.pamirs.boot.web.loader.path;

import java.util.regex.Pattern;

/**
 * 资源路径
 *
 * @author Adamancy Zhang at 20:12 on 2024-01-04
 */
public final class ResourcePath {

    public static final String PATH_SPLIT = "/";

    public static final String TYPE_SPLIT = "#";

    public static final String ALL_FLAG = "*";

    public static final String SAME_MODEL_FLAG = "$$";

    private static final int MIN_PATH_SPLIT_SIZE = 2;

    /**
     * char = '*'
     */
    public static final int ALL_FLAG_ASCLL = 42;

    public static final Pattern PATH_SPLIT_PATTERN = Pattern.compile(ResourcePath.PATH_SPLIT);

    private final ResourcePathMetadataType type;

    private final String model;

    private final String name;

    private final boolean isSameModel;

    public ResourcePath(ResourcePathMetadataType type, String model, String name) {
        this(type, model, name, false);
    }

    public ResourcePath(ResourcePathMetadataType type, String model, String name, boolean isSameModel) {
        this.type = type;
        this.model = model;
        this.name = name;
        this.isSameModel = isSameModel;
    }

    public ResourcePathMetadataType getType() {
        return type;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (this.isSameModel) {
            return this.type + ResourcePath.TYPE_SPLIT + SAME_MODEL_FLAG + ResourcePath.TYPE_SPLIT + this.name;
        }
        return this.type + ResourcePath.TYPE_SPLIT + this.model + ResourcePath.TYPE_SPLIT + this.name;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ResourcePath clone() {
        return new ResourcePath(this.type, this.model, this.name, this.isSameModel);
    }

    public static String generatorPath(String base, String... paths) {
        String basePath = ResourcePath.PATH_SPLIT + base;
        if (paths.length == 0) {
            return basePath;
        }
        StringBuilder builder = new StringBuilder(basePath);
        for (String path : paths) {
            builder.append(ResourcePath.PATH_SPLIT).append(path);
        }
        return builder.toString();
    }

    public static String resolveFirstPath(String path) {
        String[] paths = path.split(ResourcePath.PATH_SPLIT);
        if (paths.length >= MIN_PATH_SPLIT_SIZE) {
            return paths[1];
        }
        return null;
    }
}
