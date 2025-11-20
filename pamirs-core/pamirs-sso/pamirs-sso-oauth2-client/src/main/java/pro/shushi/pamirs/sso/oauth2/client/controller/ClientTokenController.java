package pro.shushi.pamirs.sso.oauth2.client.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.sso.api.utils.SsoCookieUtils;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/pamirs/sso")
public class ClientTokenController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @RequestMapping("/callback")
    private String getToken(@RequestParam("code") String code, @RequestParam(value = "state", required = false) String state) {
        //TODO 自定义
        return "";
    }

    @PostMapping("/client/logout")
    private String logout(@RequestBody Map<String, Object> map) {
        String openId = (String) map.get("openId");
        if (StringUtils.isNotEmpty(openId)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

            String sessionId = EncryptionHandler.encrypt(pamirsSsoProperties.getClient().getClientId(), openId);
            try {
                UserCache.logout();
                CookieUtil.remove(request, response, UserConstant.USER_SESSION_ID);
                String userCodeCacheKey = SsoConfigurationConstant.USER_REDIS_CACHE + openId;
                redisTemplate.delete(userCodeCacheKey);
                SsoCookieUtils.remove(request, response, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
                return "SUCCESS";
            } catch (Exception e) {
                e.printStackTrace();
                return "FAIL";
            }
        }
        return "FAIL";
    }
}