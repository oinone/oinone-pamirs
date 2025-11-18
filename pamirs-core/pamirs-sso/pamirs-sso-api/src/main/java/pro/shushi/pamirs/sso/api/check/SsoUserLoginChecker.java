package pro.shushi.pamirs.sso.api.check;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.check.UserInfoChecker;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.login.ISsoUserLoginChecker;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserOperationRecordService;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeService;
import pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker;

@Slf4j
@Component
public class SsoUserLoginChecker implements ISsoUserLoginChecker {

    @Autowired(required = false)
    private IUserDataChecker dataChecker;

    @Autowired(required = false)
    private PicVerificationCodeService verificationCodeService;

    @Autowired(required = false)
    private UserOperationRecordService userOperationRecordService;

    @Override
    public PamirsUser check4login(SsoUserVo ssoUserVo) {
        // 输入验证
        if (ssoUserVo == null || StringUtils.isBlank(ssoUserVo.getUsername()) || StringUtils.isBlank(ssoUserVo.getPassword())) {
            return null;
        }
        String username = ssoUserVo.getUsername();
        String password = ssoUserVo.getPassword();

        PamirsUserTransient user = new PamirsUserTransient();
        user.setLogin(username);
        user.setPhone(username);
        user.setEmail(username);
        user.setPassword(password);

        PamirsUser rUser = findUserByIdentifier(user);

        if (rUser == null) {
            return null;
        }

        // 密码验证
        PamirsUserDataChecker.checkPwdWithDbPwd(user, rUser);
        checkPicCode4Login(user);

        return user.getBroken() ? null : rUser;
    }

    private PamirsUser findUserByIdentifier(PamirsUserTransient user) {
        if (UserInfoChecker.checkEmail(user.getEmail())) {
            return dataChecker.checkEmailIsExist(user);
        } else if (UserInfoChecker.checkPhone(user.getPhone())) {
            return dataChecker.checkPhoneOrLoginExist(user);
        } else {
            return dataChecker.checkLoginNameNotExist(user);
        }
    }

    @Override
    public void checkPicCode4Login(PamirsUserTransient user) {
    }

}
     