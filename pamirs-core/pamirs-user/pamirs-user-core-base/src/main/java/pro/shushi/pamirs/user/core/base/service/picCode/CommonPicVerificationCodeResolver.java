package pro.shushi.pamirs.user.core.base.service.picCode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeRequestResolver;

import java.util.Optional;

/**
 * 请求图形验证码
 * <p>
 * 请求协议：
 * <p>
 * 前端请求图形验证码的时候get请求的URL上拼接参数 code=${value}&picCodeScene=common_pic_code
 * picCodeScene：当前解析器的值为common_pic_code，如果picCodeScene的值不传递，默认值也是为common_pic_code，也会执行这里
 * <p>
 * 数据存储：租户系统中将会以该code作为存储维度去存储图形验证码
 *
 * @author shier
 * date  2022/5/31 下午1:51
 */
@Component
@Slf4j
public class CommonPicVerificationCodeResolver extends PicVerificationCodeRequestResolver {

    public static final String COMMON_PIC_CODE_KEY = "code";

    @Override
    public String scene() {
        return UserConstant.COMMON_PIC_CODE;
    }

    @Override
    public String handleRequest() {
        String code = Optional.ofNullable(PamirsSession.getRequestVariables().getParameter(COMMON_PIC_CODE_KEY)).orElse(StringUtils.EMPTY);
        return code;
    }

}
