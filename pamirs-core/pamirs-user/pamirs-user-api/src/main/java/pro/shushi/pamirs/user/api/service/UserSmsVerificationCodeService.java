package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author shier
 * date  2022/9/7 10:35 下午
 */
public interface UserSmsVerificationCodeService {
    void pushPhoneVerificationCode(PamirsUserTransient userTransient, String msgType);
    void pushPhoneVerificationCodeBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew);

    void ensureVerificationCode(PamirsUserTransient userTransient, String msgType);

    void ensureVerificationCodeBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew,Boolean onlyCheck);
}
