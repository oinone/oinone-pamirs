package pro.shushi.pamirs.framework.connectors.data.plugin.util;

import pro.shushi.pamirs.meta.util.FieldUtils;

/**
 * 字段工具类
 * <p>
 * 2020/7/13 5:30 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class FieldUtil implements StatementUtil {

    public static final String NAME = "FIELD";

    public static final FieldUtil INSTANCE = new FieldUtil();

    public Object get(Object obj, String property) {
        return FieldUtils.getFieldValue(obj, property);
    }

    public boolean containsKey(Object obj, String property) {
        return FieldUtils.containsFieldValue(obj, property);
    }

}
