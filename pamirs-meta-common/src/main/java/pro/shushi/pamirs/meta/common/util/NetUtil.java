package pro.shushi.pamirs.meta.common.util;

import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * net帮助类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
public class NetUtil {

    public static String[] domains(String domain){
        return domain.split("\\" + CharacterConstants.SEPARATOR_DOT);
    }

}
