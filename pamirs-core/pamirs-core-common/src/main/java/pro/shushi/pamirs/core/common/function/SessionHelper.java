package pro.shushi.pamirs.core.common.function;

import org.apache.commons.collections4.MapUtils;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.Map;

/**
 * @author Adamancy Zhang on 2021-02-27 15:42
 */
public class SessionHelper {

    private SessionHelper() {
        //reject create object
    }

    public static Map<String, String> generatorSession() {
        return PamirsSession.fetchSessionMap();
    }

    public static void fillSession(Map<String, String> sessionContext) {
        if (sessionContext == null || MapUtils.isEmpty(sessionContext)) {
            return;
        }
        PamirsSession.fillSessionFromMap(sessionContext);
    }

    public static void clear() {
        PamirsSession.clear();
    }
}
