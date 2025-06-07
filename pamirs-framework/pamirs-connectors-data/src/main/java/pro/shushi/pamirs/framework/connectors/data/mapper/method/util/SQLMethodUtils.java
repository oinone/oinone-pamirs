package pro.shushi.pamirs.framework.connectors.data.mapper.method.util;

import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;

import java.util.Map;

/**
 * @author Adamancy Zhang at 13:01 on 2023-06-26
 */
public class SQLMethodUtils {

    public static boolean isWrapperEntityMap(IWrapper<?> iWrapper) {
        if (null == iWrapper) {
            return false;
        }
        return isEntityMap(iWrapper.getEntity());
    }

    public static <T> boolean isEntityMap(T entity) {
        return entity instanceof Map;
    }

    public static String getInsertOrUpdateSqlColumn(String column) {
        return column + SqlConstants.EQ + SqlConstants.VALUES
                + CharacterConstants.LEFT_BRACKET + column + CharacterConstants.RIGHT_BRACKET
                + CharacterConstants.SEPARATOR_COMMA;
    }
}
