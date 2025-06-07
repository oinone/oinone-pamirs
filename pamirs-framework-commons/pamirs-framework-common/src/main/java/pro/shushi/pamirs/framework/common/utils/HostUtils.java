package pro.shushi.pamirs.framework.common.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 域名处理工具类
 * <p>
 * 2022/2/23 1:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class HostUtils {

    /**
     * 获取部分域名
     *
     * @param host  域名
     * @param level 倒数段数
     * @return 部分域名
     */
    public static String fetchDomainPart(String host, int level) {
        String[] domain = host.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        int index = domain.length - level;
        if (index >= 0) {
            return domain[index];
        }
        return null;
    }

    /**
     * 判断是否是ip地址
     *
     * @param ip IP地址
     * @return 是否是ip地址
     */
    public static boolean isIPAddress(String ip) {
        int length = ip.length();
        if (length < 7 || length > 15) return false;
        String[] arr = ip.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        if (arr.length != 4) return false;
        for (int i = 0; i < 4; i++) {
            if (!StringUtils.isNumeric(arr[i])) return false;
        }
        for (int i = 0; i < 4; i++) {
            int temp = Integer.parseInt(arr[i]);
            if (temp < 0 || temp > 255) return false;
        }
        return true;
    }

}
