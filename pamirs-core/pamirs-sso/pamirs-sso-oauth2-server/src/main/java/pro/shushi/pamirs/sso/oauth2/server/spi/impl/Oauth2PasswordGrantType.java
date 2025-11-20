package pro.shushi.pamirs.sso.oauth2.server.spi.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.check.SsoUserLoginChecker;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameter;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;
import pro.shushi.pamirs.sso.api.enmu.SsoGranTypeEnum;
import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.api.dto.OAuthTokenResponse;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoClientService;
import pro.shushi.pamirs.sso.oauth2.server.spi.IUserLoginOAuth2GrantType;
import pro.shushi.pamirs.sso.oauth2.server.utils.TokenCache;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Optional;

import static pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant.*;

@Component
@SPI.Service("password")
@Order
public class Oauth2PasswordGrantType implements IUserLoginOAuth2GrantType {

    private SsoUserLoginChecker loginChecker = BeanDefinitionUtils.getBean(SsoUserLoginChecker.class);

    @Autowired
    private SsoClientService ssoClientService;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;


    @Override
    public boolean match(SsoRequestParameter ssoRequestParameter) {
        String grantType = ssoRequestParameter.getGrant_type();
        return SsoGranTypeEnum.PASSWORD.getType().equals(grantType);
    }

    @Override
    public OAuthTokenResponse execute(SsoRequestParameter ssoRequestParameter) {
        // 处理 PASSWORD 授权类型的逻辑
        OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
        SsoUserVo ssoUserVo = new SsoUserVo();
        BeanUtils.copyProperties(ssoRequestParameter, ssoUserVo);
        PamirsUser pamirsUser = login(ssoUserVo);
        if (pamirsUser != null) {
            String clientId = ssoRequestParameter.getClient_id();
            if (StringUtils.isNotBlank(clientId)) {
                SsoClient ssoClient = ssoClientService.getByClientId(clientId);
                if (ssoClient == null) {
                    throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_CLIENT_NOT_FONT_ERROR).errThrow();
                }

                Long expiresIn = Optional
                        .ofNullable(ssoClient.getExpiresIn())
                        .orElse(pamirsSsoProperties.getServer().getDefaultExpires().getExpiresIn());

                Long refreshTokenExpiresIn = Optional
                        .ofNullable(ssoClient.getRefreshTokenExpiresIn())
                        .orElse(pamirsSsoProperties.getServer().getDefaultExpires().getRefreshTokenExpiresIn());

                String openId = pamirsUser.getId().toString();

                String randomAkId = UUIDUtil.getUUIDNumberString();
                HashMap<String, String> map = new HashMap<>();
                map.put(PAMIRS_SSO_CLIENT_ID, clientId);
                map.put(PAMIRS_SSO_OPEN_ID, openId);
                map.put(PAMIRS_SSO_RANDOM_AK_ID, randomAkId);
                String accessToken = JwtTokenUtil.generateToken(JSON.toJSONString(map), expiresIn);


                HashMap<String, String> refreshMap = new HashMap<>();
                refreshMap.put("clientId", clientId);
                refreshMap.put("openId", String.valueOf(openId));
                refreshMap.put("dateTime", String.valueOf(System.currentTimeMillis()));
                String jsonString = JSON.toJSONString(refreshMap);
                String refreshToken = JwtTokenUtil.generateToken(jsonString, refreshTokenExpiresIn);
                oAuthTokenResponse.setAccessToken(accessToken);
                oAuthTokenResponse.setExpiresIn(expiresIn);
                oAuthTokenResponse.setRefreshToken(refreshToken);
                oAuthTokenResponse.setRefreshTokenExpiresIn(refreshTokenExpiresIn);

                TokenCache.putAK(accessToken, clientId, openId, randomAkId, expiresIn);
                TokenCache.putRK(refreshToken, clientId, openId, refreshTokenExpiresIn);

                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                response.addCookie(new Cookie(SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY, accessToken));
                return oAuthTokenResponse;
            }
        }
        return null;
    }


    /**
     * 登录生成Token
     *
     * @param ssoUserVo
     * @return
     */
    private PamirsUser login(SsoUserVo ssoUserVo) {
        PamirsUser rUser = loginChecker.check4login(ssoUserVo);
        if (rUser == null) {
            return null;
        }
        return rUser;
    }
}
