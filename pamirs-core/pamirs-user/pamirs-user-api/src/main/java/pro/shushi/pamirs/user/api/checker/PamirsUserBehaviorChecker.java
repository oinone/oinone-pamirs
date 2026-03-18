package pro.shushi.pamirs.user.api.checker;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.PasswordService;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeService;
import pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker;

import java.util.Objects;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;

/**
 * @author shier
 * date  2022/5/26 下午4:44
 */
@Component
@Slf4j
public class PamirsUserBehaviorChecker {

    @Autowired(required = false)
    private PicVerificationCodeService verificationCodeService;

    /**
     * 图片验证码校验是否一致
     *
     * @param inputPicCode 用户输入的验证码
     * @param login        所需要修改的账号
     */
    public void verifyPicCodeRight(String inputPicCode, String login) {
        String loginPicCode = verificationCodeService.modifyPwdVerificationCode(login);
        if (StringUtils.isBlank(loginPicCode)) {
            throw PamirsException.construct(USER_CHANGE_PWD_NO_PIC_CODE_ERROR).errThrow();
        }
        if (!loginPicCode.equalsIgnoreCase(inputPicCode)) {
            //图形验证码错误
            log.info("The graphic verification code entered by the user is incorrect. The data entered by the user is {}, the verification code in the system is {}, and the user account is {}", inputPicCode, loginPicCode, login);
            throw PamirsException.construct(USER_LOGIN_PIC_CODE_ERROR).errThrow();
        }
    }

    // TODO 使用父类数据管理器查询的数据域是不齐全的，需要使用 PamirsUser 自己的数据管理器查询
    public PamirsUser checkUserExist(Long id) {
        PamirsUser existUserData = new PamirsUser().setId(id).queryById(id);
        if (Objects.isNull(existUserData)) {
            throw PamirsException.construct(UserExpEnumerate.USER_NOT_EXIST_ERROR).errThrow();
        }
        return existUserData;
    }

    /**
     * 检查初始化密码格式
     *
     * @param pwd
     * @return
     */
    public void checkInitialPasswordFormat(String pwd) {
        if (null == pwd || !PamirsUserDataChecker.checkInitPassword(pwd)) {
            throw PamirsException.construct(USER_CREATE_AND_UPDATE_INIT_PASSWORD_FORMAT_ERROR).errThrow();
        }
    }

    public void verifyOldPasswordAndPassword(String rawPassword, PamirsUser rUser) {
        //如果是初始化密码，用初始化密码比较
        if (!BeanDefinitionUtils.getBean(PasswordService.class).checkUserPassword(rUser.getId(), rawPassword)) {
            throw PamirsException.construct(USER_OLD_NEW_PASSWORD_RAW_ERROR).errThrow();
        }
    }


    /**
     * 检查 密码以及确认密码
     *
     * @param password
     * @param confirmPassword
     */
    public void verifyConfirmPasswordAndPassword(String password, String confirmPassword) {
        if (null == password || !password.equals(confirmPassword)) {
            throw PamirsException.construct(USER_DO_NOT_MATCH_PASSWORD_ERROR).errThrow();
        }
    }

}
