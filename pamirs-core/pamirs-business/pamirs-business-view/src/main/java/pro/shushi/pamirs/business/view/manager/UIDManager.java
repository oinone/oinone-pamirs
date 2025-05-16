package pro.shushi.pamirs.business.view.manager;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import static pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate.SYSTEM_ERROR_USER;


/**
 * UIDManager
 *
 * @author yakir on 2022/09/16 16:40.
 */
public class UIDManager {

    public static Long uid() {
        Long uid = PamirsSession.getUserId();
        if (null == uid || uid < 1) {
            throw PamirsException.construct(SYSTEM_ERROR_USER)
                    .errThrow();
        }
        return uid;
    }
}
