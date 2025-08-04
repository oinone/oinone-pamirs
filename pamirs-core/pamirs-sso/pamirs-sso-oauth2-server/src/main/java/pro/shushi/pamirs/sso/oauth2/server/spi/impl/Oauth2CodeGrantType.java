package pro.shushi.pamirs.sso.oauth2.server.spi.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;
import pro.shushi.pamirs.sso.api.enmu.SsoGranTypeEnum;
import pro.shushi.pamirs.sso.api.model.SsoOauth2ClientDetails;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoOauth2ClientDetailsService;
import pro.shushi.pamirs.sso.oauth2.server.spi.IUserLoginOAuth2GrantType;
import pro.shushi.pamirs.sso.oauth2.server.utils.TokenCache;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Optional;

@Component
@SPI.Service("authorization_code")
@Order
public class Oauth2CodeGrantType implements IUserLoginOAuth2GrantType {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SsoOauth2ClientDetailsService ssoOauth2ClientDetailsService;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;


    @Override
    public boolean match(SsoRequestParameters ssoRequestParameters) {
        return SsoGranTypeEnum.CODE.getType().equals(ssoRequestParameters.getGrant_type());
    }

    @Override
    public OAuthTokenResponse execute(SsoRequestParameters ssoRequestParameters) {
        //TODO 目前只实现从请求体中拿ClientId， 第二版支持从请求头中获取clientId
        try {
            String code = ssoRequestParameters.getCode();
            if (StringUtils.isBlank(code)) {
                return null;
            }
            String clientId = ssoRequestParameters.getClient_id();
            String clientSecret = ssoRequestParameters.getClient_secret();
            if (StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
                SsoOauth2ClientDetails ssoOauth2ClientDetails = ssoOauth2ClientDetailsService.getOauth2ClientDetailsInfoByClientId(clientId);
                String clientIdEn = EncryptionHandler.decryptSecret(ssoOauth2ClientDetails.getPrivateKey(), clientSecret);
                if (clientId.equals(clientIdEn)) {
                    Long expiresIn = Optional
                            .ofNullable(ssoOauth2ClientDetails.getExpiresIn())
                            .orElse(pamirsSsoProperties.getServer().getDefaultExpires().getExpiresIn());

                    Long refreshTokenExpiresIn = Optional
                            .ofNullable(ssoOauth2ClientDetails.getRefreshTokenExpiresIn())
                            .orElse(pamirsSsoProperties.getServer().getDefaultExpires().getRefreshTokenExpiresIn());

                    Long codeExpiresIn = Optional
                            .ofNullable(ssoOauth2ClientDetails.getCodeExpiresIn())
                            .orElse(pamirsSsoProperties.getServer().getDefaultExpires().getCodeExpiresIn());

                    String redisKey = EncryptionHandler.decrypt(clientId, code);
                    String dateTime = redisKey.split(SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER)[1];
                    boolean isCodeExpires = EncryptionHandler.isCode(dateTime, codeExpiresIn);
                    if (isCodeExpires) {
                        OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
                        String openId = redisTemplate.opsForValue().getAndSet(redisKey, null);

                        String randomAkId = UUIDUtil.getUUIDNumberString();
                        HashMap<String, String> map = new HashMap<>();
                        map.put("clientId", clientId);
                        map.put("openId", openId);
                        map.put("randomAkId", randomAkId);
                        String accessToken = JwtTokenUtil.generateToken(JSON.toJSONString(map), expiresIn);

                        oAuthTokenResponse.setAccessToken(accessToken);
                        oAuthTokenResponse.setExpiresIn(expiresIn);

                        HashMap<String, String> refreshMap = new HashMap<>();
                        refreshMap.put("clientId", clientId);
                        refreshMap.put("openId", openId.toString());
                        refreshMap.put("dateTime", String.valueOf(System.currentTimeMillis()));
                        String refreshToken = JwtTokenUtil.generateToken(JSON.toJSONString(refreshMap), refreshTokenExpiresIn);
                        oAuthTokenResponse.setRefreshToken(refreshToken);
                        oAuthTokenResponse.setRefreshTokenExpiresIn(refreshTokenExpiresIn);

                        TokenCache.putAK(accessToken, clientId, openId, randomAkId, expiresIn);
                        TokenCache.putRK(refreshToken, clientId, openId, refreshTokenExpiresIn);

                        //保存在SSO服务器一份
                        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                        response.addCookie(new Cookie(SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY, accessToken));

                        return oAuthTokenResponse;
                    }
                }
            }
        } catch (Exception e) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_GET_CODE_ERROR).errThrow();
        }
        return null;
    }
}