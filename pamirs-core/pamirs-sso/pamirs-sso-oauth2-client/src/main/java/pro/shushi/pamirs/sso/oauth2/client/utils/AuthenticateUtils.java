package pro.shushi.pamirs.sso.oauth2.client.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.core.common.HttpRequestBuilder;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.core.common.enmu.HttpRequestTypeEnum;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.tmodel.ApiCommonTransient;
import pro.shushi.pamirs.sso.api.utils.SsoCookUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * 三方登录权限校验工具类
 *
 * @author wangxian
 */
@Slf4j
@Component
public class AuthenticateUtils {


//    /**
//     * 获取访问令牌
//     *
//     * @param type        授权类型：password（密码模式）、client_credentials（客户端模式）
//     * @param username    用户名
//     * @param password    密码
//     * @param redirectUrl 重定向url
//     * @return
//     */
//    public static ApiCommonTransient getAuthenticatedToken(String type, String username, String password, String redirectUrl, HttpServletRequest httpServletRequest) {
//        Map<String, Object> paramMap = new HashMap<>(9);
//        paramMap.put("grant_type", type);
//        paramMap.put("code", "");
//        paramMap.put("password", password);
//        paramMap.put("username", username);
//        paramMap.put("redirect_uri", redirectUrl);
//        paramMap.put("client_id", CLINET_ID);
//        paramMap.put("client_secret", SECRET);
//        ApiCommonTransient apiCommonTransient = null;
//        try {
//            log.info("SSO-获取访问令牌接口");
//            HttpRequest request = HttpUtil.createPost(URL + "uaa/oauth2/token");
//            request.header("client-id", CLINET_ID);
//            request.header("Content-Type", "application/x-www-form-urlencoded");
//            System.out.println(JSONUtil.toJsonStr(paramMap));
//            request.form(paramMap);
//            HttpResponse r = request.execute();
//            System.out.println(r.body());
//            apiCommonTransient = JSONUtil.toBean(r.body(),
//                    ApiCommonTransient.class);
//            apiCommonTransient = executeApiRequest(HttpUtil.createPost(URL + "uaa/oauth2/token"),
//                    null, "application/x-www-form-urlencoded", paramMap, apiCommonTransient);
//        } catch (Exception e) {
//            log.error("SSO-获取访问令牌接口 出现异常错误信息={}", e.getMessage());
//        }
//        return apiCommonTransient;
//    }

    /**
     * 获取登录用户的权限信息
     *
     * @param authorization
     * @param loginUrl
     * @return
     */
    public static ApiCommonTransient getPermissionInfo(String authorization, String loginUrl) {
        Map<String, Object> paramMap = new HashMap<>(9);
        paramMap.put("grant_type", "");
        paramMap.put("code", "");
        String uuid = UUIDUtil.getUUIDNumberString();
        ApiCommonTransient apiCommonTransient = new ApiCommonTransient();
        try {
            apiCommonTransient = executeApiRequest(URLHelper.repairDirectoryPath(loginUrl) + "/pamirs/sso/userinfo", authorization, paramMap, apiCommonTransient);
        } catch (Exception e) {
            log.error("SSO-获取登录用户的权限信息 出现异常错误信息={},{}", e.getMessage(), uuid, e);
        }
        return apiCommonTransient;
    }


    /**
     * 登出系统
     *
     * @param
     * @param loginUrl
     * @return
     */
    public static void logout(String loginUrl) {
        Map<String, Object> paramMap = new HashMap<>(0);
//        String uuid = UUIDUtil.getUUIDNumberString();
        try {
            log.info("SSO-登出系统 ");
            StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);

            Long userId = (Long) PamirsSession.getUserId();
            String userCodeCacheKey = SsoConfigurationConstant.USER_REDIS_CACHE + userId;
            String authorization = redisTemplate.opsForValue().get(userCodeCacheKey);
            executeApiRequest(URLHelper.repairDirectoryPath(loginUrl) + "/pamirs/sso/logout", "Bearer " + authorization, paramMap, null);

            //清理下登录的cookie
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            redisTemplate.delete(userCodeCacheKey);
            SsoCookUtils.remove(request, response, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
        } catch (Exception e) {
            log.error("sso logout error.", e);
        }
    }

//    /**
//     * 刷新令牌
//     *
//     * @param authorization
//     * @return
//     */
//    public static ApiCommonTransient refreshToken(String authorization, String refreshToken) {
//        Map<String, Object> paramMap = new HashMap<>(9);
//        paramMap.put("refreshToken", refreshToken);
//
//        ApiCommonTransient apiCommonTransient = null;
//        try {
//            log.info("SSO-获取访问令牌接口");
//            apiCommonTransient = executeApiRequest(URL + "uaa/auth/refresh-token",
//                    "Bearer " + authorization, paramMap, apiCommonTransient);
//        } catch (Exception e) {
//            log.error("SSO-获取访问令牌接口 出现异常错误信息={}", e.getMessage());
//        }
//        return apiCommonTransient;
//    }

    private static ApiCommonTransient executeApiRequest(String url, String authorization,
                                                        Map<String, Object> paramMap,
                                                        ApiCommonTransient apiCommonTransient) throws IOException {
        PamirsSsoProperties pamirsSsoProperties = BeanDefinitionUtils.getBean(PamirsSsoProperties.class);
        paramMap.put("client-id", pamirsSsoProperties.getClient().getClientId());
        paramMap.put("Authorization", authorization);
        String result = HttpRequestBuilder.newInstance(url, HttpRequestTypeEnum.POST)
                .addParams(paramMap)
                .request();
        apiCommonTransient = JSON.parseObject(result, ApiCommonTransient.class);
        return apiCommonTransient;
    }


}
