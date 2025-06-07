package pro.shushi.pamirs.user.core.base.service.picCode;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeRequestResolver;

/**
 * 登录请求图形验证码
 *
 * @author shier
 * date  2022/5/31 下午1:51
 */
@Component
public class LoginPicVerificationCodeResolver extends PicVerificationCodeRequestResolver {

    public static final String LOGIN_PIC_CODE_KEY = "login";

    @Override
    public String scene() {
        return UserConstant.LOGIN_PIC_CODE;
    }

    @Override
    public String handleRequest() {
        /**
         * 请求URL路径中的参数 login：用户登录所使用的账号/邮箱/手机号
         */
        return PamirsSession.getRequestVariables().getParameter(LOGIN_PIC_CODE_KEY);
    }
}
