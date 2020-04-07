package pro.shushi.pamirs.meta.common.util;

import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.UUID;

/**
 * id帮助类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
public class UUIDUtil {

    public static UUID getUUID(){
        return UUID.randomUUID();
    }

    public static String getUUIDString(){
        return UUID.randomUUID().toString();
    }

    public static String getUUIDNumberString(){
        return UUID.randomUUID().toString().replaceAll(CharacterConstants.SEPARATOR_HYPHEN,"");
    }

}
