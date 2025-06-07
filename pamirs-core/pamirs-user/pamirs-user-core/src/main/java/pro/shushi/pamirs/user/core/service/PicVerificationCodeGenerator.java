package pro.shushi.pamirs.user.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeGeneratorApi;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeRequestHandler;
import pro.shushi.pamirs.user.api.utils.VerifyCodeUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shier
 * date  2022/8/12 4:55 下午
 */
@Slf4j
@Component
public class PicVerificationCodeGenerator implements PicVerificationCodeGeneratorApi {

    @Autowired
    private PicVerificationCodeRequestHandler handler;

    @Override
    public String generate(HttpServletRequest request) {
        String verifyCode = VerifyCodeUtil.randomStr(4);
        handler.storePicVerificationCode(request, verifyCode);
        return verifyCode;
    }
}
