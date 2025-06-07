package pro.shushi.pamirs.framework.connectors.data.ddl.utils;

import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ddl工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class DdlUtils {

    public static boolean notEqualsIgnoreNull(String s1, String s2) {
        if (null == s1) {
            s1 = CharacterConstants.SEPARATOR_EMPTY;
        }
        if (null == s2) {
            s2 = CharacterConstants.SEPARATOR_EMPTY;
        }
        return !s1.equals(s2);
    }

    public static String fixIdentifyLength(String identifyName, int length, boolean inverse) {
        if (identifyName.length() > length) {
            if (inverse) {
                identifyName = identifyName.substring(identifyName.length() - length);
            } else {
                identifyName = identifyName.substring(0, length);
            }
        }
        return identifyName;
    }

    @SuppressWarnings("unused")
    public static List<String> getDbDdl(Map<String/*db*/, List<String>> listMap, String db) {
        listMap.computeIfAbsent(db, k -> new ArrayList<>());
        return listMap.get(db);
    }

    public static String buildString(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            if (null != string) {
                sb.append(string);
            }
        }
        return sb.toString();
    }

}
