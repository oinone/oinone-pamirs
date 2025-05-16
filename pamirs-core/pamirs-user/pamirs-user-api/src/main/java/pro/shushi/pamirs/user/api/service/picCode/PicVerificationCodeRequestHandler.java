package pro.shushi.pamirs.user.api.service.picCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.constants.UserConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author shier
 * date  2022/5/31 下午2:05
 */
@Component
//@Service
public class PicVerificationCodeRequestHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 验证码的场景
     */
    public final static String PARAM_PIC_CODE_SCENE = "picCodeScene";

    private static Map<String, PicVerificationCodeRequestResolver> innerMap;

    public static Map<String, PicVerificationCodeRequestResolver> getInnerMap() {
        Map<String, PicVerificationCodeRequestResolver> innerMap = PicVerificationCodeRequestHandler.innerMap;
        if (innerMap == null) {
            synchronized (PicVerificationCodeRequestHandler.class) {
                innerMap = PicVerificationCodeRequestHandler.innerMap;
                if (innerMap == null) {
                    Map<String, PicVerificationCodeRequestResolver> beanMap = BeanDefinitionUtils.getBeansOfType(PicVerificationCodeRequestResolver.class);
                    innerMap = new HashMap<>(beanMap.size());
                    for (PicVerificationCodeRequestResolver resolver : beanMap.values()) {
                        innerMap.put(resolver.scene(), resolver);
                    }
                    PicVerificationCodeRequestHandler.innerMap = innerMap;
                }
            }
        }
        return innerMap;
    }

    public String resolveCode() {
        String picCodeScene = Optional.ofNullable(PamirsSession.getRequestVariables().getParameter(PARAM_PIC_CODE_SCENE)).orElse(UserConstant.COMMON_PIC_CODE);
        Map<String, PicVerificationCodeRequestResolver> innerMap = getInnerMap();
        PicVerificationCodeRequestResolver resolver = Optional.ofNullable(innerMap.get(picCodeScene)).orElse(innerMap.get(UserConstant.COMMON_PIC_CODE));
        return resolver.handleRequest();
    }

    public void storePicVerificationCode(HttpServletRequest request, String picCode) {
        String picCodeScene = Optional.ofNullable(request.getParameter(PARAM_PIC_CODE_SCENE)).orElse(UserConstant.COMMON_PIC_CODE);
        Map<String, PicVerificationCodeRequestResolver> innerMap = getInnerMap();
        PicVerificationCodeRequestResolver resolver = Optional.ofNullable(innerMap.get(picCodeScene)).orElse(innerMap.get(UserConstant.COMMON_PIC_CODE));
        String code = resolver.handleRequest();
        storePicVerificationCode(code, picCodeScene, picCode);
    }

    public void storePicVerificationCode(String code, String picCodeScene, String picCode) {
        Map<String, PicVerificationCodeRequestResolver> innerMap = getInnerMap();
        PicVerificationCodeRequestResolver resolver = Optional.ofNullable(innerMap.get(picCodeScene)).orElse(innerMap.get(UserConstant.COMMON_PIC_CODE));
        String key = resolver.generateStoreKey(code);
        redisTemplate.opsForValue().set(key, picCode, 300, TimeUnit.SECONDS);
    }

    public String getPicVerificationCode(String code, String picCodeScene) {
        Map<String, PicVerificationCodeRequestResolver> innerMap = getInnerMap();
        PicVerificationCodeRequestResolver resolver = Optional.ofNullable(innerMap.get(picCodeScene)).orElse(innerMap.get(UserConstant.COMMON_PIC_CODE));
        String key = resolver.generateStoreKey(code);
        return redisTemplate.opsForValue().get(key);
    }

}
