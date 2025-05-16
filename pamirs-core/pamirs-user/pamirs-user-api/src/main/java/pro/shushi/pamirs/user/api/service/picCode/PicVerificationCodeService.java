package pro.shushi.pamirs.user.api.service.picCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.user.api.constants.UserConstant;

/**
 * 图片验证码服务
 * @author shier
 * date  2022/5/27 下午2:07
 */
@Component
public class PicVerificationCodeService {

    @Autowired
    private PicVerificationCodeRequestHandler picVerificationCodeHandler;

    /**
     * 获取用户的登录的时候的验证码
     * @param login
     * @return
     */
   public String loginVerificationCode(String login) {
        return picVerificationCodeHandler.getPicVerificationCode(login, UserConstant.LOGIN_PIC_CODE);
    }

    /**
     *
     * 获取更新密码的验证码
     *
     * @param login
     * @return
     */
    public String modifyPwdVerificationCode(String login) {
        return picVerificationCodeHandler.getPicVerificationCode(login, UserConstant.MODIFY_PWD_PIC_CODE);
    }

    
}
