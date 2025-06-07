package pro.shushi.pamirs.user.api.login;

import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author shier
 * date  2022/9/7 9:08 下午
 */
public interface IUserLoginChecker {

    PamirsUser check4login(PamirsUserTransient user);

    void checkPicCode4Login(PamirsUserTransient userTransient);
}
