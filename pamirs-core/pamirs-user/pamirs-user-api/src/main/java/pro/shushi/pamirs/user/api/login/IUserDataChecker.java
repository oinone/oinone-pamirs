package pro.shushi.pamirs.user.api.login;

import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author shier
 * date  2022/9/7 9:28 下午
 */
public interface IUserDataChecker {

    PamirsUser checkPhoneExist(PamirsUserTransient userTransient);

    /**
     * 检查手机号或者登录名是否存在
     * @param userTransient
     * @return
     */
    PamirsUser checkPhoneOrLoginExist(PamirsUserTransient userTransient);

    void checkInviteCode(PamirsUserTransient userTransient);

    void checkLoginNameIsExist(PamirsUserTransient userTransient);

    PamirsUser checkLoginNameNotExist(PamirsUserTransient userTransient);

    PamirsUser checkEmailIsExist(PamirsUserTransient userTransient);

    PamirsUser checkEmailIsExist(PamirsUserTransient userTransient,Boolean isNew);

    void checkEmailExist(PamirsUserTransient userTransient);

    void checkEmailExist(PamirsUserTransient userTransient,Boolean isNew);

    /**
     * 校验手机是否存在
     * @param userTransient
     * @param isNew 是否校验新手机
     * @return
     */
    PamirsUser checkPhoneExist(PamirsUserTransient userTransient,Boolean isNew);

    /**
     * 校验手机是否已被注册
     * @param userTransient
     * @param isNew 是否校验新手机
     * @return
     */
    void checkPhoneIsNotExist(PamirsUserTransient userTransient,Boolean isNew);

    /**
     * 校验邮箱是否已被注册
     * @param userTransient
     * @param isNew 是否校验新邮箱
     * @return
     */
    void checkEmailIsNotExist(PamirsUserTransient userTransient,Boolean isNew);
}

