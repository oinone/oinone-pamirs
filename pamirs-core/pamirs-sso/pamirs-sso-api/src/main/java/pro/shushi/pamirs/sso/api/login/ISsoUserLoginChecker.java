package pro.shushi.pamirs.sso.api.login;

import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author shier
 * date  2022/9/7 9:08 下午
 */
public interface ISsoUserLoginChecker {

    PamirsUser check4login(SsoUserVo ssoUserVo);

    void checkPicCode4Login(PamirsUserTransient user);
}
