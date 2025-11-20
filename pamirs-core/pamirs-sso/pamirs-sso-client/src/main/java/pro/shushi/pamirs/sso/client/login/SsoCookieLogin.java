package pro.shushi.pamirs.sso.client.login;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.sso.client.utils.Oauth2AuthenticateUtils;
import pro.shushi.pamirs.sso.common.dto.Result;
import pro.shushi.pamirs.sso.common.dto.SsoUserInfo;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.login.IUserLoginChecker;
import pro.shushi.pamirs.user.api.login.UserCookieLogin;
import pro.shushi.pamirs.user.api.login.UserInfoCache;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Slf4j
@Order(0)
@Component
public class SsoCookieLogin extends UserCookieLogin<PamirsUser> {

    private IUserLoginChecker checker;

    @Autowired(required = false)
    private IUserDataChecker dataChecker;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    public String createSessionId(PamirsUser pamirsUser) {
        return EncryptionHandler.encrypt(pamirsSsoProperties.getClient().getClientId(), pamirsUser.getId().toString() + "#" + UUIDUtil.getUUIDNumberString());
    }

    @Override
    public String type() {
        return UserLoginTypeEnum.COOKIE.value();
    }

    @Override
    public PamirsUser resolveAndVerification(PamirsUserTransient user) {
        if (checker == null) {
            checker = BeanDefinitionUtils.getBean(IUserLoginChecker.class);
        }
        String phone = user.getPhone();
        String email = user.getEmail();
        if (StringUtils.isNotBlank(email)) {
            return dataChecker.checkEmailIsExist(user);
        } else if (StringUtils.isNotBlank(phone)) {
            return dataChecker.checkPhoneExist(user);
        }
        return checker.check4login(user);
    }


    /**
     * 重写登录拦截功能
     * 该函数主要作用,通过三方权限校验.
     *
     * @return
     */
    @Override
    public PamirsUserDTO fetchUserIdByReq() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return fetchUserIdByReq4Pamirs(null);
        }
        PamirsRequestVariables pamirsRequestVariables = PamirsSession.getRequestVariables();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        PamirsUserDTO pamirsUserDTO = fetchUserIdByReq4Pamirs(request);
        if (pamirsUserDTO != null) {
            // FIXME: zbh 20240702 CookieUtil.getValue会导致登录异常
//            String authorization = CookieUtil.getValue(request, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
//            updateAuthorizationToken(authorization, pamirsUserDTO.getUserId());
            return pamirsUserDTO;
        }
        // Handle null user
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        String[] referers = Optional.ofNullable(pamirsRequestVariables.getHeaders())
                .map(v -> v.get("referer"))
                .map(URI::create)
                .map(URI::getQuery)
                .map(v -> v.split("="))
                .orElse(null);
        String accessToken = "";
        if (referers != null && referers.length > 0 && "accessToken".equals(referers[0])) {
            accessToken = referers[1];
        } else {
            accessToken = pamirsRequestVariables.getHeader("authorization");
        }
        if (StringUtils.isNotEmpty(accessToken)) {
            Result<SsoUserInfo> permissionInfo = Oauth2AuthenticateUtils.getPermissionInfo(accessToken);
            // Check for successful login
            if (Result.SUCCESS_CODE.equals(permissionInfo.getCode())) {
                // SSO user changes to Oinone user
                PamirsUser pamirsUser = setUserInfoToCookiesAndSetUserIdToCache(permissionInfo, accessToken, response);
                return new PamirsUserDTO().setUserId(pamirsUser.getId()).setPhone(pamirsUser.getPhone()).setUserCode(
                        pamirsUser.getCode()).setLogin(pamirsUser.getLogin()).setEmail(pamirsUser.getEmail()).setUserName(pamirsUser.getName());
            }
        }
        return null;
    }

    private void updateAuthorizationToken(String authorization, Long userId) {
        if (StringUtils.isNotEmpty(authorization)) {
            String userCodeCacheKey = SsoConfigurationConstant.USER_REDIS_CACHE + userId;
            String redisToken = redisTemplate.opsForValue().get(userCodeCacheKey);
            if (StringUtils.isEmpty(redisToken) || !authorization.equals(redisToken)) {
                redisTemplate.opsForValue().set(userCodeCacheKey, authorization, pamirsSsoProperties.getClient().getExpires().getExpiresIn(), TimeUnit.SECONDS);
            }
        }
    }


    /**
     * 开放一个用户登录setCookies函数 供登录和跳转校验
     *
     * @param permissionInfo
     * @param accessToken
     * @param response
     * @return
     */
    public PamirsUser setUserInfoToCookiesAndSetUserIdToCache(Result<SsoUserInfo> permissionInfo, String accessToken, HttpServletResponse response) {
        SsoUserInfo ssoUser = permissionInfo.getData();
        PamirsUser pamirsUser = userService.queryById(ssoUser.getId());
        if (pamirsUser == null) {
            pamirsUser = createOrUpdatePamirsUser(ssoUser);
        }

        String sessionId = createSessionId(pamirsUser);
        PamirsSession.setSessionId(sessionId);
        String cacheKey = parseSessionId(sessionId);
        UserCache.putCache(cacheKey, coverToUserDTO(pamirsUser));
        try {
            CookieUtil.set(response, UserConstant.USER_SESSION_ID, sessionId);
            CookieUtil.set(response, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY, accessToken);
        } catch (Exception e) {
            log.error("SSO Login Cookie Set Err", e);
        }

        String userCodeCacheKey = SsoConfigurationConstant.USER_REDIS_CACHE + pamirsUser.getId();
        redisTemplate.opsForValue().set(userCodeCacheKey, accessToken, pamirsSsoProperties.getClient().getExpires().getExpiresIn(), TimeUnit.SECONDS);
        return pamirsUser;
    }

    private PamirsUser createOrUpdatePamirsUser(SsoUserInfo ssoUser) {
        PamirsUser pamirsUser = buildPamirsUser(ssoUser);
        userService.createOrUpdate(pamirsUser);
        return pamirsUser;
    }

    private PamirsUser buildPamirsUser(SsoUserInfo ssoUser) {
        PamirsUser pamirsUser = new PamirsUser();
        pamirsUser.setId(ssoUser.getId());
        pamirsUser.setCode(ssoUser.getCode());
        pamirsUser.setPhone(ssoUser.getPhone());
        pamirsUser.setEmail(ssoUser.getEmail());
        pamirsUser.setActive(ssoUser.getActive());
        pamirsUser.setName(ssoUser.getName());
        pamirsUser.setNickname(ssoUser.getNickname());
        return pamirsUser;
    }

    /**
     * 原始用户登陆后函数调用
     *
     * @param request
     * @return
     */
    private PamirsUserDTO fetchUserIdByReq4Pamirs(HttpServletRequest request) {
        PamirsUserDTO pamirsUserDTO = super.fetchUserIdByReq();
        if (pamirsUserDTO == null || pamirsUserDTO.getUserId() == null) {
            return pamirsUserDTO;
        }
        PamirsUser user = UserInfoCache.queryUserById(pamirsUserDTO.getUserId());
        if (user != null && !Boolean.TRUE.equals(user.getActive())) {
            //清理下登录的cookie
            logout();

            log.error("{}当前用户是{},{}", UserExpEnumerate.USER_CAN_NOT_ACTIVE_ERROR, pamirsUserDTO.getUserId(), pamirsUserDTO.getLogin());
            throw PamirsException.construct(UserExpEnumerate.USER_CAN_NOT_ACTIVE_ERROR).errThrow();
        }
        return pamirsUserDTO;
    }
}
