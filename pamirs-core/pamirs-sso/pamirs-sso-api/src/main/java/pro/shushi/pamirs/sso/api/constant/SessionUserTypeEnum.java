package pro.shushi.pamirs.sso.api.constant;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

/**
 * @author wangxian
 */
@Dict(displayName = "Session用户类型", dictionary = SessionUserTypeEnum.DICTIONARY)
public class SessionUserTypeEnum extends BaseEnum<SessionUserTypeEnum, String> {

    public static final String DICTIONARY = "sso.common.SessionUserTypeEnum";

    public final static SessionUserTypeEnum SYSTEM = create("SYSTEM", "SYSTEM", "系统用户", "系统用户");
    public final static SessionUserTypeEnum COMPANY_ADMIN = create("COMPANY_ADMIN", "COMPANY_ADMIN", "业主超级管理员", "业主超级管理员");
    public final static SessionUserTypeEnum COMPANY_USER = create("COMPANY_USER", "COMPANY_USER", "业主普通用户", "业主普通用户");

}
