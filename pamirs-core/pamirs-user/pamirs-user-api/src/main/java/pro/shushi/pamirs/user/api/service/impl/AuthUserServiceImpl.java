package pro.shushi.pamirs.user.api.service.impl;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.user.AuthUser;
import pro.shushi.pamirs.auth.api.user.AuthUserService;
import pro.shushi.pamirs.user.api.constants.SystemUser;
import pro.shushi.pamirs.user.api.model.PamirsPassword;

/**
 * @author Adamancy Zhang at 15:39 on 2024-01-06
 */
@Component
public class AuthUserServiceImpl implements AuthUserService {

    @Override
    public AuthUser getAdminUser() {
        return SystemUser.admin();
    }

    @Override
    public AuthUser getAnonymousUser() {
        return SystemUser.anonymous();
    }

    @Override
    public String getPasswordModel() {
        return PamirsPassword.MODEL_MODEL;
    }
}
