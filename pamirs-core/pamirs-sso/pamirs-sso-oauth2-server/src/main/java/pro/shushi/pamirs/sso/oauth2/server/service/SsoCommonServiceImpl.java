package pro.shushi.pamirs.sso.oauth2.server.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.enmu.SsoAuthTypeEnum;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;
import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.api.service.SsoCommonService;
import pro.shushi.pamirs.sso.api.service.SsoOauth2TokenService;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoClientService;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SsoCommonServiceImpl implements SsoCommonService {
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;
    @Autowired
    private SsoClientService ssoClientService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SsoOauth2TokenService ssoOauth2TokenService;

    @Override
    public void checkAuth(String clientId, String redirectUri, String state) {
        SsoAuthTypeEnum authType = pamirsSsoProperties.getServer().getAuthType();
        SsoClient ssoClient = ssoClientService.getSsoClientInfoByClientId(clientId);
        if (Objects.nonNull(ssoClient) && null != ssoClient.getAuthType()) {
            authType = ssoClient.getAuthType();
        }
        String url = "";
        // 已登录
        if (null != PamirsSession.getUserId() && null != ssoClient) {
            if (SsoAuthTypeEnum.OAUTH2.equals(ssoClient.getAuthType())) {
                String code = ssoOauth2TokenService.getOauth2Code(ssoClient, PamirsSession.getUserId());
                url = redirectUri + "?code=" + code;
                if (StringUtils.isNotBlank(state)) {
                    url += "&state=" + state;
                }
            }
        } else {
            try {
                redirectUri = URLEncoder.encode(redirectUri, "UTF-8");
            } catch (Exception ignored) {
            }
            url = pamirsSsoProperties.getServer().getLoginUrl() + ";client_id=" + clientId +
                    ";redirect_uri=" + redirectUri + ";type=" + authType;
            if (StringUtils.isNotEmpty(state)) {
                url += ";state=" + state;
            }
        }

        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getResponse();
        try {
            Objects.requireNonNull(response).sendRedirect(url);
        } catch (Exception e) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_REDIRECT_PAGE_ERROR, e).errThrow();
        }
    }

    @Override
    public String getKey(String username) {
        try {
            KeyPair keyPair = EncryptHelper.getRSAKeyPair();
            String publicKey = EncryptHelper.getKey(keyPair.getPublic());
            String privateKey = EncryptHelper.getKey(keyPair.getPrivate());
            String key = SsoConfigurationConstant.PAMIRS_SSO_PRIVATE_KEY_PREFIX + username;
            redisTemplate.opsForValue().set(key, privateKey, 5, TimeUnit.MINUTES);
            return publicKey;
        } catch (Exception e) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_GET_PASSWORD_PUBLIC_ERROR).errThrow();
        }
    }
}
