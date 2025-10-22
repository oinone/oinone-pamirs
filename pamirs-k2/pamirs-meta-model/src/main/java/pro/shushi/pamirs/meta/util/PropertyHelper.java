package pro.shushi.pamirs.meta.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SystemProperty帮助类
 *
 * @author Adamancy Zhang at 10:42 on 2024-07-11
 */
public class PropertyHelper {

    private static final String BOOLEAN_TRUE_VALUE = Boolean.TRUE.toString();

    private static final String BOOLEAN_FALSE_VALUE = Boolean.FALSE.toString();

    /**
     * 获取整数配置值
     *
     * @param key          配置Key
     * @param defaultValue 默认值
     * @return 整数配置值
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            String value = System.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException ignored) {
        }
        return defaultValue;
    }

    /**
     * 获取布尔配置值
     *
     * @param key          配置Key
     * @param defaultValue 默认值
     * @return 布尔配置值
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = System.getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            if (BOOLEAN_TRUE_VALUE.equalsIgnoreCase(value)) {
                return true;
            }
            if (BOOLEAN_FALSE_VALUE.equalsIgnoreCase(value)) {
                return false;
            }
        }
        return defaultValue;
    }

    /**
     * 获取多个字符串配置值
     *
     * @param key 配置Key
     * @return 多个字符串配置值
     */
    public static Set<String> getSetProperties(String key) {
        String value = System.getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            return Arrays.stream(value.split(CharacterConstants.SEPARATOR_COMMA)).map(String::trim).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
