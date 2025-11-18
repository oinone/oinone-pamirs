package pro.shushi.pamirs.sso.oauth2.server.spi.impl;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoClientService;
import pro.shushi.pamirs.sso.oauth2.server.spi.IOAuth2RefreshToken;
import pro.shushi.pamirs.sso.oauth2.server.utils.TokenCache;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@SPI.Service("refresh_token")
@Order
public class OAuth2RefreshToken implements IOAuth2RefreshToken {

    @Autowired
    private SsoClientService ssoClientService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @Override
    public OAuthTokenResponse execute(SsoRequestParameters ssoRequestParameters) {
        try {
            String tokenHead = SsoConfigurationConstant.OAUTH2_PREFIX;
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authorization = request.getHeader(SsoConfigurationConstant.PAMIRS_SSO_TOKEN_HEADER);
            if (authorization != null && authorization.startsWith(tokenHead)) {
                authorization = EncryptionHandler.decryptBase64(authorization.substring(tokenHead.length()));
                String[] clientIdAndSecret = authorization.split(SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER);
                String clientId = clientIdAndSecret[0];
                String clientSecret = clientIdAndSecret[1];
                SsoClient ssoClient = ssoClientService.getSsoClientInfoByClientId(clientId);
                Long expiresIn = Optional.ofNullable(ssoClient.getExpiresIn()).orElse(pamirsSsoProperties.getServer().getDefaultExpires().getExpiresIn());

                Long refreshTokenExpiresIn = Optional.ofNullable(ssoClient.getRefreshTokenExpiresIn()).orElse(pamirsSsoProperties.getServer().getDefaultExpires().getRefreshTokenExpiresIn());

                String privateKey = ssoClient.getPrivateKey();
                String clientIdEn = EncryptionHandler.decryptSecret(privateKey, clientSecret);
                if (clientId.equals(clientIdEn)) {
                    String refreshToken = ssoRequestParameters.getRefresh_token();
                    if (refreshToken != null) {
                        String authToken = JwtTokenUtil.getKeyFromToken(refreshToken);
                        Map<String, String> refreshMap = JSON.parseObject(authToken, Map.class);
                        String refreshClientId = refreshMap.get("clientId");
                        String openId = refreshMap.get("openId");
                        String dateTime = refreshMap.get("dateTime");
                        String redisRefToken = TokenCache.getRK(refreshClientId, openId);
                        if (clientId.equals(refreshClientId) && refreshToken.equals(redisRefToken)) {
                            boolean timeoutOrNot = EncryptionHandler.isCode(dateTime, refreshTokenExpiresIn);
                            if (timeoutOrNot) {

                                String randomAkId = UUIDUtil.getUUIDNumberString();
                                HashMap<String, String> accessMap = new HashMap<>();
                                accessMap.put("clientId", clientId);
                                accessMap.put("openId", openId);
                                accessMap.put("randomAkId", randomAkId);
                                String accessToken = JwtTokenUtil.generateToken(JSON.toJSONString(accessMap), expiresIn);

                                OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
                                oAuthTokenResponse.setAccessToken(accessToken);
                                oAuthTokenResponse.setExpiresIn(expiresIn);
                                // 标记上一次的AccessToken 10分钟过期 ，
                                Long cacheTokenExpirationTime = Optional.ofNullable(ssoClient.getCacheTokenExpirationTime()).orElse(pamirsSsoProperties.getServer().getDefaultExpires().getCacheTokenExpirationTime());
                                TokenCache.markExpireSoon(clientId, openId, cacheTokenExpirationTime);
                                //  更新AccessToken
                                TokenCache.putRefreshAK(accessToken, clientId, openId, expiresIn);
                                return oAuthTokenResponse;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
