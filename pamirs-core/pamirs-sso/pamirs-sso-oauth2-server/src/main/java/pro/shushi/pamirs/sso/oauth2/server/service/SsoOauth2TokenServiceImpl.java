package pro.shushi.pamirs.sso.oauth2.server.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;
import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.api.model.UserRelSsoClient;
import pro.shushi.pamirs.sso.api.service.SsoCommonService;
import pro.shushi.pamirs.sso.api.service.SsoOauth2TokenService;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.sso.api.utils.SsoCookieUtils;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoClientService;
import pro.shushi.pamirs.sso.oauth2.server.model.UserRelSsoClientService;
import pro.shushi.pamirs.sso.oauth2.server.spi.IOAuth2RefreshToken;
import pro.shushi.pamirs.sso.oauth2.server.spi.IUserLoginOAuth2GrantType;
import pro.shushi.pamirs.sso.oauth2.server.utils.TokenCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SsoOauth2TokenServiceImpl implements SsoOauth2TokenService {

    private SsoUserLoginChecker loginChecker = BeanDefinitionUtils.getBean(SsoUserLoginChecker.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SsoClientService ssoClientService;
    @Autowired
    private UserService userService;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;
    @Autowired
    private SsoCommonService ssoCommonService;
    @Autowired
    private UserRelSsoClientService userRelSsoClientService;

    @Autowired(required = false)
    @Qualifier(AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR)
    private ExecutorService globalFixedThreadPoolExecutor;


    /**
     * 校验是否获取到用户
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
    public OAuthTokenResponse refresh(SsoRequestParameters ssoRequestParameters) {
        IOAuth2RefreshToken auth2RefreshToken = Spider.getLoader(IOAuth2RefreshToken.class).getOrderedExtensions().get(0);
        OAuthTokenResponse oAuthTokenResponse = auth2RefreshToken.execute(ssoRequestParameters);
        if (oAuthTokenResponse != null) {
            return oAuthTokenResponse;
        }
        throw PamirsException.construct(SsoExpEnumerate.SSO_REFRESH_TOKEN_ERROR).errThrow();
    }


    @Override
    public PamirsUser getUserInfo(String clientId) {
        String tokenHead = UserConstant.USER_TOKEN_PREFIX;
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith(tokenHead)) {
            authorization = authorization.substring(tokenHead.length());
            String resource = JwtTokenUtil.getKeyFromToken(authorization);
            Map<String, String> mapResource = JSON.parseObject(resource, Map.class);
            String cacheClientId = mapResource.get("clientId");
            String openId = mapResource.get("openId");
            String randomAkId = mapResource.get("randomAkId");
            String redisAccessToken = TokenCache.getAK(cacheClientId, openId, randomAkId);
            if (clientId.equals(cacheClientId) && !clientId.equals(pamirsSsoProperties.getClient().getClientId()) && authorization.equals(redisAccessToken)) {  // 客户端传过来是小令牌
                return userService.queryById(Long.parseLong(openId));
            } else if (cacheClientId.equals(pamirsSsoProperties.getClient().getClientId()) && authorization.equals(redisAccessToken)) { //大令牌直接登录
                return userService.queryById(Long.parseLong(openId));
            }
        }
        throw PamirsException.construct(SsoExpEnumerate.SSO_INVALID_ACCESS_TOKEN_ERROR).errThrow();
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
                List<UserRelSsoClient> userRelSsoClients = userRelSsoClientService.queryListByUserId(Long.parseLong(openId));
                List<SsoClient> ssoClients = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(userRelSsoClients)) {
                    List<String> ssoClientIds = userRelSsoClients.stream().map(UserRelSsoClient::getSsoClientId).collect(Collectors.toList());
                    if (!ssoClientIds.contains(clientId)){
                        ssoClientIds.add(clientId);
                    }
                    ssoClients = ssoClientService.queryListByClientIds(ssoClientIds);
                }else {
                    ssoClients.add(ssoClientService.getSsoClientInfoByClientId(clientId));
                }
                try {
                    if (CollectionUtils.isNotEmpty(ssoClients)){
                        logoutAndRemove(clientId, openId, ssoClients);
                        TokenCache.cleanAK(clientId, openId, randomAkId);
                    }
                } catch (IOException e) {
                    throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_LOGOUT_ERROR).errThrow();
                }
            } else if (cacheClientId.equals(pamirsSsoProperties.getClient().getClientId()) && authorization.equals(redisAccessToken)) { //大令牌直接登录
                try {
                    List<SsoClient> list = ssoClientService.getClientInfos();
                    logoutAndRemove(clientId, openId, list);
                    TokenCache.cleanAK(cacheClientId, openId, randomAkId);
                } catch (IOException e) {
                    throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_LOGOUT_ERROR).errThrow();
                }
            }
        }
    }

    private void logoutAndRemove(String clientId, String openId, List<SsoClient> list) throws IOException {
        HashMap<String, Object> param = new HashMap<>();
        param.put("openId", openId);
        //TODO 执行异步持续通知 存在以下问题
        //  1. 高并发异步（危） 2. 异步回调通知延迟，SSO端持续发送将下一次登录端用户杀掉（误杀）
        // CallbackRetryExample.callBackLogout(list, param);

        ExecutorHelper.execute(globalFixedThreadPoolExecutor, () -> {
            try {
                for (SsoClient ssoClient : list) {
//                    if (ssoOauth2ClientDetails.getClientId().equals(clientId)) continue;
                    HttpRequestBuilder.newInstance(ssoClient.getLogoutUrl(), HttpRequestTypeEnum.POST).addParams(param).request();
                }
            } catch (Throwable e) {
                log.error("refresh homepage error.", e);
            }
        });


        //清空cookie
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SsoCookieUtils.remove(request, response, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
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
        if (StringUtils.isBlank(ssoUserVo.getClientId())) return;
        PamirsUser pamirsUser = checkLogin(ssoUserVo);
        String clientId = ssoUserVo.getClientId();

        SsoClient ssoClient = ssoClientService.getSsoClientInfoByClientId(clientId);
        String code = getOauth2Code(ssoClient, pamirsUser.getId());
        if (StringUtils.isBlank(code)) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_GET_CODE_ERROR).errThrow();
        }
        String url = ssoUserVo.getRedirectUri() + "?code=" + code;
        if (StringUtils.isNotBlank(ssoUserVo.getState())) {
            url += "&state=" + ssoUserVo.getState();
        }
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        try {
            Objects.requireNonNull(response).sendRedirect(url);
        } catch (Exception e) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_REDIRECT_PAGE_ERROR, e).errThrow();
        }
    }

    @Override
    public String getOauth2Code(SsoClient ssoClient, Long userId) {
        if (ssoClient == null) {
            return null;
        }
        Long codeExpiresIn = Optional.ofNullable(ssoClient.getCodeExpiresIn()).orElse(pamirsSsoProperties.getServer().getDefaultExpires().getCodeExpiresIn());
        String redisKey = UUIDUtil.getUUIDNumberString() + ":" + System.currentTimeMillis();
        String code = EncryptionHandler.encrypt(ssoClient.getClientId(), redisKey);
        redisTemplate.opsForValue().set(redisKey, userId.toString(), codeExpiresIn, TimeUnit.SECONDS);
        return code;
    }
}
