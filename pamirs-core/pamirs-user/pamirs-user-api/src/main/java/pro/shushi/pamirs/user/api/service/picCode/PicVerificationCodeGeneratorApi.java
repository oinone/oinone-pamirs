package pro.shushi.pamirs.user.api.service.picCode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 图片验证码的生成
 *
 * @author shier
 * date  2022/5/27 下午2:07
 */
public interface PicVerificationCodeGeneratorApi {

    /**
     * @return
     */
    String generate(HttpServletRequest request);


}
