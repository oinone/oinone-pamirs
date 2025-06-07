package pro.shushi.pamirs.framework.connectors.data.sql.utils;

import pro.shushi.pamirs.framework.connectors.data.sql.contants.SqlLike;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * SqlUtils工具类
 */
public class SqlUtils {

    /**
     * 用%连接like
     *
     * @param str 原字符串
     * @return like 的值
     */
    public static String concatLike(Object str, SqlLike type) {
        switch (type) {
            case LEFT:
                return CharacterConstants.PERCENT + str;
            case RIGHT:
                return str + CharacterConstants.PERCENT;
            default:
                return CharacterConstants.PERCENT + str + CharacterConstants.PERCENT;
        }
    }
}
