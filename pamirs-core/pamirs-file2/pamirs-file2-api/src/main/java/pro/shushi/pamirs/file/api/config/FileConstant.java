package pro.shushi.pamirs.file.api.config;

import java.util.regex.Pattern;

public interface FileConstant {

    /**
     * 属性分隔符（正则）
     */
    Pattern REGEX_POINT_CHARACTER = Pattern.compile("\\.");

    /**
     * 属性分隔符
     */
    String POINT_CHARACTER = ".";

    /**
     * 数组标识符（正则）
     */
    Pattern REGEX_LIST_FLAG_CHARACTER = Pattern.compile("\\[\\*]");

    /**
     * 数组标识符
     */
    String LIST_FLAG_CHARACTER = "[*]";

    /**
     * <h>内置属性转换分隔符（field -> key）</h>
     * <p>
     * e.g. EasyExcel
     * 固定表头: object.id -> {.object#id}
     * 固定格式: object.id -> {object#id}
     * </p>
     */
    String SEPARATION_CHARACTER = "#";

    /**
     * 块属性默认前缀
     */
    String BLOCK_PREFIX = "data";

    String CSV_IMPORT_CHARSET = "GB2312";

    String CSV_EXPORT_CHARSET = "GB2312";

    String CSV_FILE_SUFFIX = ".csv";

    String DEFAULT_TEMPLATE_NAME = "DEFAULT_TEMPLATE";
}
