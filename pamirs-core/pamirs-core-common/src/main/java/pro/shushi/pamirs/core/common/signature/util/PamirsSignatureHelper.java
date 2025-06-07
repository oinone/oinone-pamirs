package pro.shushi.pamirs.core.common.signature.util;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.signature.PamirsSignature;

/**
 * 签名对象帮助类
 *
 * @author Adamancy Zhang on 2021-05-09 14:31
 */
public class PamirsSignatureHelper {

    private PamirsSignatureHelper() {
        //reject create object
    }

    public static boolean equals(PamirsSignature o1, PamirsSignature o2) {
        if (ObjectUtils.allNotNull(o1, o2)) {
            return o1.equals(o2);
        }
        return false;
    }

    public static boolean equalsBySignature(PamirsSignature o1, PamirsSignature o2) {
        if (ObjectUtils.allNotNull(o1, o2)) {
            String o1s = o1.signature(),
                    o2s = o2.signature();
            if (StringUtils.isAnyBlank(o1s, o2s)) {
                return false;
            }
            return o1s.equals(o2s);
        }
        return false;
    }
}
