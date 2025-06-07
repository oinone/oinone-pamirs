package pro.shushi.pamirs.user.api.checker;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.check.UserInfoChecker;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.login.IUserLoginChecker;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserOperationRecordService;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeService;
import pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker;

import java.util.Optional;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;

/**
 * @author shier
 * date  2022/9/7 10:56 下午
 */
@Component
@Slf4j
public class UserLoginChecker implements IUserLoginChecker {

    @Autowired(required = false)
    private IUserDataChecker dataChecker;

    @Autowired(required = false)
    private PicVerificationCodeService verificationCodeService;

    @Autowired(required = false)
    private UserOperationRecordService userOperationRecordService;

    @Override
    public PamirsUser check4login(PamirsUserTransient user) {
        if (user.getBroken()) {
            return null;
        }
        String login = user.getLogin();
        String phone = user.getPhone();
        String email = user.getEmail();
        PamirsUser rUser = null;
        if (StringUtils.isNotBlank(email)) {
            rUser = dataChecker.checkEmailIsExist(user);
        } else if (StringUtils.isNotBlank(phone)) {
            rUser = dataChecker.checkPhoneExist(user);
        } else if (StringUtils.isNotBlank(login)) {
            if (UserInfoChecker.checkPhone(login)) {
                //手机号也可能是用户名(用手机号作为用户名/手机号为空的情况)
                user.setPhone(login);
                user.setLogin(login);
                rUser = dataChecker.checkPhoneOrLoginExist(user);
            } else if (UserInfoChecker.checkEmail(login)) {
                user.setEmail(login);
                rUser = dataChecker.checkEmailIsExist(user);
            } else {
                rUser = dataChecker.checkLoginNameNotExist(user);
            }
        }
        PamirsUserDataChecker.checkPwdWithDbPwd(user, rUser);
        checkPicCode4Login(user);
        return rUser;
    }


    /**
     * 密码输入在60s内错误三次之后,需要图片验证码进行登录
     *
     * @param userTransient
     */
    @Override
    public void checkPicCode4Login(PamirsUserTransient userTransient) {
        Integer count = 0;
        String loginPicCode = verificationCodeService.loginVerificationCode(userTransient.getLogin());
        //校验图形验证码
        String inputPicCode = Optional.ofNullable(userTransient.getPicCode()).orElse(StringUtils.EMPTY);
        if (userTransient.getBroken()) {
            //用户输入的信息错误
            count = userOperationRecordService.recordLoginErrorCount(userTransient.getLogin());
            if (count == 3) {
                log.error("错误次数在60s内超过了三次需要输入图形验证码，用户输入的登录账号/手机号/email为{}", userTransient.getLogin());
                broken(userTransient.setErrorMsg(USER_LOGIN_PIC_CODE_ERROR.msg())
                        .setErrorCode(USER_LOGIN_PIC_CODE_ERROR.code())
                        .setErrorField(UserConstant.FIELD_PIC_CODE));
//                throw PamirsException.construct(UserExpEnumerate.USER_NEED_PIC_CODE_ERROR).errThrow();
            }
        } else {
            count = userOperationRecordService.getLoginErrorCount(userTransient.getLogin());
            if (count < 3 && (StringUtils.isNotEmpty(inputPicCode) || null != loginPicCode) && !inputPicCode.equalsIgnoreCase(loginPicCode)) {
                broken(userTransient.setErrorMsg(USER_LOGIN_PIC_CODE_ERROR.msg())
                        .setErrorCode(USER_LOGIN_PIC_CODE_ERROR.code())
                        .setErrorField(UserConstant.FIELD_PIC_CODE)
                );
                return;
            }
        }

        if (count >= 3) {
            if (StringUtils.isEmpty(inputPicCode)) {
                broken(userTransient.setErrorMsg(USER_NEED_PIC_CODE_ERROR.msg())
                        .setErrorCode(USER_NEED_PIC_CODE_ERROR.code())
                        .setErrorField(UserConstant.FIELD_PIC_CODE)
                );
            } else if (StringUtils.isEmpty(loginPicCode)) {
                broken(userTransient.setErrorMsg(USER_LOGIN_PIC_CODE_ERROR.msg())
                        .setErrorCode(USER_REFRESH_PIC_CODE_ERROR.code())
                        .setErrorField(UserConstant.FIELD_PIC_CODE)
                );
            } else if (!loginPicCode.equalsIgnoreCase(inputPicCode)) {
                broken(userTransient.setErrorMsg(USER_LOGIN_PIC_CODE_ERROR.msg())
                        .setErrorCode(USER_LOGIN_PIC_CODE_ERROR.code())
                        .setErrorField(UserConstant.FIELD_PIC_CODE));
            }
        }

    }

    private <R> boolean checkLoginAndFieldPair(Getter<PamirsUser, R> getter, R value) {
        IWrapper<PamirsUser> qw = Pops.<PamirsUser>lambdaQuery()
                .from(PamirsUser.MODEL_MODEL)
                .eq(getter, value);
        return Models.origin().count(qw) > 0;
    }
}
