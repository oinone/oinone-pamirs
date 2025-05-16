package pro.shushi.pamirs.framework.compute.system.check.util;

import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;

/**
 * 检查提交
 * <p>
 * 2020/7/3 11:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class CheckCommitter {

    public static boolean commit(boolean checkPerArgument, boolean checkPerObject, boolean returnWhenError,
                                 Committer committer, String extra, Object... args) {
        boolean result = true;
        if (checkPerArgument) {
            if (ArrayUtils.isNotEmpty(args)) {
                for (Object arg : args) {
                    if (null != arg && checkPerObject) {
                        if (arg instanceof List) {
                            for (Object obj : (List<?>) arg) {
                                result = result(committer.commit(obj), extra, returnWhenError) && result;
                            }
                        } else if (arg.getClass().isArray()) {
                            for (Object obj : (Object[]) arg) {
                                result = result(committer.commit(obj), extra, returnWhenError) && result;
                            }
                        } else {
                            result = result(committer.commit(arg), extra, returnWhenError) && result;
                        }
                    } else {
                        result = result(committer.commit(arg), extra, returnWhenError) && result;
                    }
                }
            }
        } else {
            result = result(committer.commit(args), extra, returnWhenError);
        }
        return result;
    }

    public interface Committer {
        Object commit(Object arg);
    }

    private static boolean result(Object result, String extra, boolean returnWhenError) {
        boolean isSuccess;
        if (!(result instanceof Boolean)) {
            isSuccess = PamirsSession.getMessageHub().isSuccess();
        } else {
            isSuccess = (Boolean) result;
        }
        if (!isSuccess && returnWhenError) {
            throw PamirsException.construct(FwExpEnumerate.BASE_CHECK_DATA_ERROR).appendMsg(extra).errThrow();
        }
        return isSuccess;
    }

}
