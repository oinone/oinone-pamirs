package pro.shushi.pamirs.framework.connectors.data.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 配置文件工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 4:38 上午
 */
public class ConfigureUtils {

    public static String substringForNamespace(String path, String prefix) {
        String subKey = path.replace(prefix + CharacterConstants.SEPARATOR_DOT, CharacterConstants.SEPARATOR_EMPTY);
        if (StringUtils.isBlank(subKey)) {
            return null;
        }
        return StringUtils.substringBefore(subKey, CharacterConstants.SEPARATOR_DOT);
    }

}
