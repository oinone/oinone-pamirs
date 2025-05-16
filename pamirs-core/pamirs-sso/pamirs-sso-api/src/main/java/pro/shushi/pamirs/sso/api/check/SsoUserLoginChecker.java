package pro.shushi.pamirs.sso.api.check;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.check.UserInfoChecker;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.login.ISsoUserLoginChecker;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserOperationRecordService;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeService;
import pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker;

import java.util.Optional;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;

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
        PamirsUserTransient user = new PamirsUserTransient();
        user.setLogin(ssoUserVo.getUsername());
        user.setPhone(ssoUserVo.getUsername());
        user.setEmail(ssoUserVo.getUsername());
        user.setPassword(ssoUserVo.getPassword());

        String username = ssoUserVo.getUsername();
        String phone = ssoUserVo.getUsername();
        String email = ssoUserVo.getUsername();
        PamirsUser rUser = null;
        if (StringUtils.isNotBlank(email) && UserInfoChecker.checkEmail(email)) {
            rUser = dataChecker.checkEmailIsExist(user);
        } else if (StringUtils.isNotBlank(phone) && UserInfoChecker.checkPhone(phone)) {
            rUser = dataChecker.checkPhoneExist(user);
        } else if (StringUtils.isNotBlank(username)) {
            if (UserInfoChecker.checkPhone(username)) {
                //手机号也可能是用户名(用手机号作为用户名/手机号为空的情况)
                user.setPhone(username);
                user.setLogin(username);
                rUser = dataChecker.checkPhoneOrLoginExist(user);
            } else if (UserInfoChecker.checkEmail(username)) {
                user.setEmail(username);
                rUser = dataChecker.checkEmailIsExist(user);
            } else {
                rUser = dataChecker.checkLoginNameNotExist(user);
            }
        }
        if (rUser == null) {
            return null;
        }
        PamirsUserDataChecker.checkPwdWithDbPwd(user, rUser);
        checkPicCode4Login(user);
        if (user.getBroken()) {
            return null;
        }
        return rUser;
    }

    @Override
    public void checkPicCode4Login(PamirsUserTransient user) {
    }

}
     