package pro.shushi.pamirs.framework.common.utils.header;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Exception Helper
 *
 * @author Adamancy Zhang at 11:11 on 2023-06-29
 */
public class ExceptionHelper {

    /**
     * ORACLE/DM SQL State 23000
     */
    private static final String DUPLICATE_KEY_SQL_STATE = "23000";

    /**
     * DM7 SQL State 22000
     */
    private static final String DM7_DUPLICATE_KEY_SQL_STATE = "22000";

    /**
     * DM7 Error Code -6602
     */
    private static final int DM7_DUPLICATE_KEY_ERROR_CODE = -6602;

    private ExceptionHelper() {
        //reject create object
    }

    public static boolean isDuplicateKeyException(Throwable e) {
        if (e instanceof DuplicateKeyException) {
            return true;
        } else if (e instanceof DataIntegrityViolationException) {
            // KDB duplicate key exception
            if (e.getMessage() != null && e.getMessage().contains("duplicate key")) {
                return true;
            }
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            // Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                return true;
            }
        }
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlException = (SQLException) cause;
            String sqlState = sqlException.getSQLState();
            if (DUPLICATE_KEY_SQL_STATE.equals(sqlState)) {
                return true;
            }
            int errorCode = sqlException.getErrorCode();
            return DM7_DUPLICATE_KEY_SQL_STATE.equals(sqlState) && errorCode == DM7_DUPLICATE_KEY_ERROR_CODE;
        }
        return false;
    }

    /**
     * 尝试追溯3个异常原因获取{@link PamirsException}
     *
     * @param e 任意异常
     * @return {@link PamirsException}
     */
    public static PamirsException fetchPamirsException(Throwable e) {
        if (e instanceof PamirsException) {
            return (PamirsException) e;
        }
        Throwable cause = e.getCause();
        for (int i = 0; i < 3; i++) {
            if (cause == null) {
                return null;
            }
            if (cause instanceof PamirsException) {
                return (PamirsException) cause;
            }
            cause = cause.getCause();
        }
        return null;
    }
}