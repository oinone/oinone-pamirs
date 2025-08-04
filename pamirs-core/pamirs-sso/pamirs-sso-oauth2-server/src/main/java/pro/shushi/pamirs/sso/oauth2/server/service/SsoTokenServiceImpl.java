package pro.shushi.pamirs.sso.oauth2.server.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.ExecutorHelper;
import pro.shushi.pamirs.core.common.HttpRequestBuilder;
import pro.shushi.pamirs.core.common.enmu.HttpRequestTypeEnum;
import pro.shushi.pamirs.framework.common.config.AsyncTaskExecutorConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.check.SsoUserLoginChecker;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.HttpConstant;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;
import pro.shushi.pamirs.sso.api.model.SsoOauth2ClientDetails;
import pro.shushi.pamirs.sso.api.service.SsoTokenService;
import pro.shushi.pamirs.sso.api.tmodel.ApiCommonTransient;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.sso.api.utils.Result;
import pro.shushi.pamirs.sso.api.utils.SsoCookUtils;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoOauth2ClientDetailsService;
import pro.shushi.pamirs.sso.oauth2.server.spi.IOAuth2RefreshToken;
import pro.shushi.pamirs.sso.oauth2.server.spi.IUserLoginOAuth2GrantType;
import pro.shushi.pamirs.sso.oauth2.server.utils.TokenCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SsoTokenServiceImpl implements SsoTokenService {

    private SsoUserLoginChecker loginChecker = BeanDefinitionUtils.getBean(SsoUserLoginChecker.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SsoOauth2ClientDetailsService ssoOauth2ClientDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @Autowired(required = false)
    @Qualifier(AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR)
    private ExecutorService globalFixedThreadPoolExecutor;


    /**
     * 登录生成Token
     *
     * @param ssoUserVo
     * @return
     */
    private PamirsUser checkLogin(SsoUserVo ssoUserVo) {
        PamirsUser rUser = loginChecker.check4login(ssoUserVo);

        if (rUser == null) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_LOGIN_PASSWORD_ERROR).errThrow();
        }
        return rUser;
    }


    @Override
    public Result getPrivateKey(String username) {
        try {
            KeyPair keyPair = EncryptHelper.getRSAKeyPair();
            String publicKey = EncryptHelper.getKey(keyPair.getPublic());
            String privateKey = EncryptHelper.getKey(keyPair.getPrivate());
            String key = SsoConfigurationConstant.PAMIRS_SSO_PRIVATE_KEY_PREFIX + username;
            redisTemplate.opsForValue().set(key, privateKey, 5, TimeUnit.MINUTES);
            return Result.success(publicKey);
        } catch (NoSuchAlgorithmException e) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_GET_PASSWORD_PUBLIC_ERROR).errThrow();
        }
    }

    @Override
    public OAuthTokenResponse refresh(SsoRequestParameters ssoRequestParameters) {
        IOAuth2RefreshToken auth2RefreshToken = Spider.getLoader(IOAuth2RefreshToken.class).getOrderedExtensions().get(0);
        OAuthTokenResponse oAuthTokenResponse = auth2RefreshToken.execute(ssoRequestParameters);
        if (oAuthTokenResponse != null) {
            return oAuthTokenResponse;
        }
        throw PamirsException.construct(SsoExpEnumerate.SSO_REFRESH_TOKEN_ERROR).errThrow();
    }


    @Override
    public ApiCommonTransient getUserInfo(Map<String, Object> map) {
        ApiCommonTransient apiCommonTransient = new ApiCommonTransient();
        String tokenHead = UserConstant.USER_TOKEN_PREFIX;
        String clientId = (String) map.get("client-id");
        String authorization = (String) map.get("Authorization");
        if (authorization != null && authorization.startsWith(tokenHead)) {
            authorization = authorization.substring(tokenHead.length());
            String resource = JwtTokenUtil.getKeyFromToken(authorization);
            Map<String, String> mapResource = JSON.parseObject(resource, Map.class);
            String cacheClientId = mapResource.get("clientId");
            String openId = mapResource.get("openId");
            String randomAkId = mapResource.get("randomAkId");
            String redisAccessToken = TokenCache.getAK(cacheClientId, openId, randomAkId);
            if (clientId.equals(cacheClientId) && !clientId.equals(pamirsSsoProperties.getClient().getClientId()) && authorization.equals(redisAccessToken)) {  // 客户端传过来是小令牌
                PamirsUser pamirsUser = userService.queryById(Long.parseLong(openId));
                apiCommonTransient.setCode(HttpConstant.SUCCESS);
                apiCommonTransient.setData(JSON.toJSONString(pamirsUser));
                return apiCommonTransient;
            } else if (cacheClientId.equals(pamirsSsoProperties.getClient().getClientId()) && authorization.equals(redisAccessToken)) { //大令牌直接登录
                PamirsUser pamirsUser = userService.queryById(Long.parseLong(openId));
                apiCommonTransient.setCode(HttpConstant.SUCCESS);
                apiCommonTransient.setData(JSON.toJSONString(pamirsUser));
                return apiCommonTransient;
            }
        }
        apiCommonTransient.setCode(HttpConstant.UN_AUTHENTICATE);
        apiCommonTransient.setMsg("invalid openid");
        return apiCommonTransient;
    }

    @Override
    public void logout(Map<String, Object> map) {
        String tokenHead = UserConstant.USER_TOKEN_PREFIX;
        String clientId = (String) map.get("client-id");
        String authorization = (String) map.get("Authorization");
        if (authorization != null && authorization.startsWith(tokenHead)) {
            authorization = authorization.substring(tokenHead.length());
            String resource = JwtTokenUtil.getKeyFromToken(authorization);
            Map<String, String> mapResource = JSON.parseObject(resource, Map.class);
            String cacheClientId = mapResource.get("clientId");
            String openId = mapResource.get("openId");
            String randomAkId = mapResource.get("randomAkId");

            String redisAccessToken = TokenCache.getAK(cacheClientId, openId, randomAkId);
            if (clientId.equals(cacheClientId) && !clientId.equals(pamirsSsoProperties.getClient().getClientId()) && authorization.equals(redisAccessToken)) {  // 客户端传过来是小令牌
                try {
                    SsoOauth2ClientDetails ssoOauth2ClientDetails = ssoOauth2ClientDetailsService.getOauth2ClientDetailsInfoByClientId(clientId);
                    List<SsoOauth2ClientDetails> list = new ArrayList<>();
                    list.add(ssoOauth2ClientDetails);
                    logoutAndRemove(clientId, openId, list);
                    TokenCache.cleanAK(clientId, openId, randomAkId);
                } catch (IOException e) {
                    throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_LOGOUT_ERROR).errThrow();
                }
            } else if (cacheClientId.equals(pamirsSsoProperties.getClient().getClientId()) && authorization.equals(redisAccessToken)) { //大令牌直接登录
                try {
                    List<SsoOauth2ClientDetails> list = ssoOauth2ClientDetailsService.getOauth2ClientDetailsInfos();
                    logoutAndRemove(clientId, openId, list);
                    TokenCache.cleanAK(cacheClientId, openId, randomAkId);
                } catch (IOException e) {
                    throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_LOGOUT_ERROR).errThrow();
                }
            }
        }
    }

    private void logoutAndRemove(String clientId, String openId, List<SsoOauth2ClientDetails> list) throws IOException {
        HashMap<String, Object> param = new HashMap<>();
        param.put("openId", openId);
        //TODO 执行异步持续通知 存在以下问题
        //  1. 高并发异步（危） 2. 异步回调通知延迟，SSO端持续发送将下一次登录端用户杀掉（误杀）
        // CallbackRetryExample.callBackLogout(list, param);

        ExecutorHelper.execute(globalFixedThreadPoolExecutor, () -> {
            try {
                for (SsoOauth2ClientDetails ssoOauth2ClientDetails : list) {
//                    if (ssoOauth2ClientDetails.getClientId().equals(clientId)) continue;
                    HttpRequestBuilder.newInstance(ssoOauth2ClientDetails.getLogoutUrl(), HttpRequestTypeEnum.POST).addParams(param).request();
                }
            } catch (Throwable e) {
                log.error("refresh homepage error.", e);
            }
        });


        //清空cookie
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SsoCookUtils.remove(request, response, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
    }


    @Override
    public OAuthTokenResponse authorize(SsoRequestParameters ssoRequestParameters) {
        List<IUserLoginOAuth2GrantType> orderedExtensions = Spider.getLoader(IUserLoginOAuth2GrantType.class).getOrderedExtensions();
        for (IUserLoginOAuth2GrantType orderedExtension : orderedExtensions) {
            boolean match = orderedExtension.match(ssoRequestParameters);
            if (match) {
                OAuthTokenResponse ssoOAuthResponseResult = orderedExtension.execute(ssoRequestParameters);
                if (ssoOAuthResponseResult != null) {
                    return ssoOAuthResponseResult;
                }
            }
        }
        throw PamirsException.construct(SsoExpEnumerate.SSO_GENERATE_ACCESS_TOKEN_ERROR).errThrow();
    }


    @Override
    public void login(SsoUserVo ssoUserVo) {
        //TODO 密码校验逻辑未写
        PamirsUser pamirsUser = checkLogin(ssoUserVo);
        if (pamirsUser != null) {
            try {
                String clientId = ssoUserVo.getClientId();
                if (clientId != null) {
                    SsoOauth2ClientDetails oauth2ClientDetailsInfo = ssoOauth2ClientDetailsService.getOauth2ClientDetailsInfoByClientId(clientId);

                    Long codeExpiresIn = Optional.ofNullable(oauth2ClientDetailsInfo.getCodeExpiresIn()).orElse(pamirsSsoProperties.getServer().getDefaultExpires().getCodeExpiresIn());

                    String redisKey = UUIDUtil.getUUIDNumberString() + ":" + System.currentTimeMillis();
                    String code = EncryptionHandler.encrypt(ssoUserVo.getClientId(), redisKey);
                    redisTemplate.opsForValue().set(redisKey, pamirsUser.getId().toString(), codeExpiresIn, TimeUnit.SECONDS);

                    String url = ssoUserVo.getRedirectUri() + "?code=" + code;
                    if (StringUtils.isNotBlank(ssoUserVo.getState())) {
                        url += "&state=" + ssoUserVo.getState();
                    }
                    //TODO 回调 用户在页面写到回调地址
                    //HttpRequestBuilder.newInstance(url, HttpRequestTypeEnum.GET).request();
                    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                    response.sendRedirect(url);
                }
            } catch (Exception e) {
                throw PamirsException.construct(SsoExpEnumerate.SSO_REDIRECT_PAGE_ERROR, e).errThrow();
            }
        }
    }
}
