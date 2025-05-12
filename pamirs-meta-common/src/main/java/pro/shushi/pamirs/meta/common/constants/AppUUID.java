package pro.shushi.pamirs.meta.common.constants;

import pro.shushi.pamirs.meta.common.util.UUIDUtil;

/**
 * APP uuid
 *
 * @author Adamancy Zhang at 21:51 on 2024-10-25
 */
public class AppUUID {

    private static final String UUID = UUIDUtil.getUUIDNumberString();

    public static String get() {
        return UUID;
    }
}
