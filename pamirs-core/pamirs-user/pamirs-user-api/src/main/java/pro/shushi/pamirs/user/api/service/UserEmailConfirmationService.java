package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author haibo(xf.z @ shushi.pro)
 * @date 2022-09-19 10:04:55
 */
public interface UserEmailConfirmationService {
    void sendEmailConfirmation(PamirsUserTransient userTransient, String msgType);

    void sendEmailConfirmationBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew);

    void ensureEmailConfirmation(PamirsUserTransient userTransient, String msgType);

    void ensureEmailConfirmationBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew, Boolean onlyCheck);

    void sendEmailConfirmationBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew, UserBehaviorEventEnum userBehaviorEventEnum);

}
