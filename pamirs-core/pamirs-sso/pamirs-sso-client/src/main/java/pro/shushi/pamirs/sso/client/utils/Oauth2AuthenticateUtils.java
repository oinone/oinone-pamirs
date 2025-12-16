package pro.shushi.pamirs.sso.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.utils.SsoCookieUtils;
import pro.shushi.pamirs.sso.common.dto.OAuthTokenResponse;
import pro.shushi.pamirs.sso.common.dto.Result;
import pro.shushi.pamirs.sso.common.dto.SsoUserInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/***
 * 三方登录权限校验工具类
 *
 * @author wangxian
 */
@Slf4j
@Component
public class Oauth2AuthenticateUtils {

    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    public static Result<OAuthTokenResponse> getAccessTokenInfo(String grantType, String code) {
        PamirsSsoProperties pamirsSsoProperties = BeanDefinitionUtils.getBean(PamirsSsoProperties.class);
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put("grant_type", grantType);
        paramMap.put("client_id", pamirsSsoProperties.getClient().getClientId());
        paramMap.put("client_secret", pamirsSsoProperties.getClient().getClientSecret());
        paramMap.put("code", code);
        Result<OAuthTokenResponse> result = new Result();
        try {
            String responseContent = HttpUtils.doPost(URLHelper.repairDirectoryPath(pamirsSsoProperties.getClient().getSsoServerUrl()) + "/pamirs/sso/oauth2/authorize", null, null, paramMap);
            result = JSON.parseObject(responseContent, new TypeReference<Result<OAuthTokenResponse>>() {});
        } catch (Exception e) {
            log.error("SSO-根据授权码获取授权token 出现异常错误信息={}", e.getMessage());
        }
        return result;
    }

    /**
     * 根据token获取登录用户的权限信息
     */
    public static Result<SsoUserInfo> getPermissionInfo(String authorization) {
        PamirsSsoProperties pamirsSsoProperties = BeanDefinitionUtils.getBean(PamirsSsoProperties.class);
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Bearer " + authorization);
        Map<String, String> paramMap = new HashMap<>(1);
        paramMap.put("client_id", pamirsSsoProperties.getClient().getClientId());
        Result<SsoUserInfo> result = new Result();
        try {
            String responseContent = HttpUtils.doPost(URLHelper.repairDirectoryPath(pamirsSsoProperties.getClient().getSsoServerUrl()) + "/pamirs/sso/oauth2/getUserInfo", headers, null, paramMap);
            result = JSON.parseObject(responseContent, new TypeReference<Result<SsoUserInfo>>() {});
        } catch (Exception e) {
            log.error("SSO-获取登录用户的权限信息 出现异常错误信息={}", e.getMessage());
        }
        return result;
    }

    /**
     * 登出系统
     *
     */
    public static void logout() {
        PamirsSsoProperties pamirsSsoProperties = BeanDefinitionUtils.getBean(PamirsSsoProperties.class);
        try {
            log.info("SSO-登出系统 ");
            StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);

            Long userId = PamirsSession.getUserId();
            String userCodeCacheKey = SsoConfigurationConstant.USER_REDIS_CACHE + userId;
            String authorization = redisTemplate.opsForValue().get(userCodeCacheKey);
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Authorization", "Bearer " + authorization);
            Map<String, String> paramMap = new HashMap<>(1);
            paramMap.put("client_id", pamirsSsoProperties.getClient().getClientId());

            HttpUtils.doPost(URLHelper.repairDirectoryPath(pamirsSsoProperties.getClient().getSsoServerUrl()) + "/pamirs/sso/oauth2/logout", headers, null, paramMap);

            //清理下登录的cookie
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            redisTemplate.delete(userCodeCacheKey);
            SsoCookieUtils.remove(request, response, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
        } catch (Exception e) {
            log.error("sso logout error.", e);
        }
    }
}
