package pro.shushi.pamirs.user.view.picCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.user.api.service.picCode.PicVerificationCodeGeneratorApi;
import pro.shushi.pamirs.user.api.utils.VerifyCodeUtil;

/**
 * 生成图形验证码
 *
 * @author shier
 * date  2020/9/17 6:51 下午
 */
@RestController
@Slf4j
public class PicCodeController {

    @Autowired
    PicVerificationCodeGeneratorApi codeGenerator;

    /**
     * 获取图形验证码
     * 支持的场景：用户登录/用户修改密码
     *
     * @param request  param中的值：picCodeScene（验证码场景，在登录/修改密码场景下必须传递,系统支持场景：默认场景/登录场景/忘记密码/修改密码）
     *                 <p>
     *                 param中的值：login (登录场景下必须传递,图形验证码在系统中存储的KEY)
     *                 param中的值：code (默认场景下传递，可以不传递，默认值为""，图形验证码在系统中存储的KEY)
     * @param response 将验证码图片返回给客户端
     * @return
     */
    @RequestMapping(value = "/pamirs/api/refreshPicCode", method = RequestMethod.GET)
    public String refreshPicCode(HttpServletRequest request,
                                 HttpServletResponse response) {
        // 设置浏览器不缓存本页
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Expires", "0");
        // 输出验证码给客户端
        response.setContentType("image/jpeg");
        String verifyCode = codeGenerator.generate(request);
        log.info("生成的验证码是：{}", verifyCode);
        VerifyCodeUtil.drawImage(response, verifyCode);
        return "SUCCESS";
    }

}
