package pro.shushi.pamirs.user.core.base.service.picCode;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.login.IUserLogin;
import pro.shushi.pamirs.user.api.login.LoginTypeParser;
import pro.shushi.pamirs.user.api.login.UserLoginFactory;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeRequestResolver;

/**
 * 修改密码请求图形验证码
 *
 * @author shier
 * date  2022/5/31 下午1:51
 */
@Component
public class ModifyPwdPicVerificationCodeResolver extends PicVerificationCodeRequestResolver {

    @Override
    public String scene() {
        return UserConstant.MODIFY_PWD_PIC_CODE;
    }

    @Override
    public String handleRequest() {
        IUserLogin userLogin = UserLoginFactory.getUserLogin(LoginTypeParser.getLoginType());
        if (null == userLogin) {
            throw PamirsException.construct(UserExpEnumerate.USER_CHANGE_PWD_FAIL_ERROR).errThrow();
        }
        PamirsUserDTO userDTO = userLogin.fetchUserIdByReq();
        if (null == userDTO) {
            throw PamirsException.construct(UserExpEnumerate.USER_CHANGE_PWD_NO_USER).errThrow();
        }
        String login = userDTO.getLogin();
        return login;
    }

}
