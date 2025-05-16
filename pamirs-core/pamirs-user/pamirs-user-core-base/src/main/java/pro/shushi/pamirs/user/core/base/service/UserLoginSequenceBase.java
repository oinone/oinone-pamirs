package pro.shushi.pamirs.user.core.base.service;

import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * UserLoginSequenceImpl
 *
 * @author yakir on 2022/10/18 19:36.
 */
abstract
public class UserLoginSequenceBase {

    public static final String BC_USER_LOGIN_SEQ = "BC_USER_LOGIN_SEQ";

    public String loginSequence() {
        Object seqObj = CommonApiFactory.getSequenceGenerator().generate(SequenceEnum.ORDERLY_SEQ.value(), BC_USER_LOGIN_SEQ);
        return TypeUtils.stringValueOf(seqObj);
    }
}
