package pro.shushi.pamirs.boot.standard.checker.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据源环境帮助类
 *
 * @author Adamancy Zhang at 15:17 on 2024-10-16
 */
public class DataSourceEnvironmentHelper {

    private static final Pattern DATA_SOURCE_URL_PATTERN = Pattern.compile("pamirs.datasource\\[(.*)].url");

    private static final Pattern DS_MAP_PATTERN = Pattern.compile("pamirs.framework.data.dsMap\\[(.*)]");

    private DataSourceEnvironmentHelper() {
        // reject create object
    }

    public static String getEnvironmentDsKey(String key) {
        Matcher matcher = DATA_SOURCE_URL_PATTERN.matcher(key);
        if (matcher.matches()) {
            return key.substring(18, key.length() - 5);
        }
        return null;
    }

    public static String getEnvironmentDsKeyNotNull(String key) {
        String dsKey = DataSourceEnvironmentHelper.getEnvironmentDsKey(key);
        if (dsKey == null) {
            throw new IllegalArgumentException("Invalid dsKey. environment key: " + key);
        }
        return dsKey;
    }

    public static String getEnvironmentDsMapModule(String key) {
        Matcher matcher = DS_MAP_PATTERN.matcher(key);
        if (matcher.matches()) {
            return key.substring(28, key.length() - 1);
        }
        return null;
    }
}
