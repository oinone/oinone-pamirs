package pro.shushi.pamirs.boot.common.util;

import pro.shushi.pamirs.boot.common.api.contants.MetaOnlineEnum;

/**
 * MetaOnlineLocalUtil
 *
 * @author yakir on 2022/12/29 14:34.
 */
public class MetaOnlineLocalUtil {

    public static boolean metaOnline() {
        MetaOnlineEnum metaOnline = ApplicationArgUtils.getArgs().getMetaOnline();
        return null != metaOnline
                && !MetaOnlineEnum.ALL.equals(metaOnline)
                && !MetaOnlineEnum.READ.equals(metaOnline);
    }

    @Deprecated
    public static boolean loadLocal() {
        return metaOnline();
    }

}
