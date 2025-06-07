package pro.shushi.pamirs.sso.oauth2.server.hook;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoOauth2ClientDetailsService;
import pro.shushi.pamirs.sso.oauth2.server.utils.TokenCache;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.utils.CookieUtil;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


@Component
public class SsoUserLoginAfterHook implements HookAfter {

    @Autowired
    private SsoOauth2ClientDetailsService ssoOauth2ClientDetailsService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @Override
    @Hook(priority = Integer.MAX_VALUE, displayName = "sso 登录")
    public Object run(Function function, Object ret) {
        FunctionDefinition functionDefinition = function.getFunctionDefinition();
        if (PamirsSession.getUserId() != null && PamirsUserTransient.MODEL_MODEL.equals(functionDefinition.getNamespace()) && "login".equals(functionDefinition.getFun())) {
            try {
                String clientId = UUIDUtil.getUUIDNumberString();
                Long openId = PamirsSession.getUserId();
                Long expiresIn = pamirsSsoProperties.getServer().getDefaultExpires().getExpiresIn();
                Long refreshTokenExpiresIn = pamirsSsoProperties.getServer().getDefaultExpires().getRefreshTokenExpiresIn();

                String randomAkId = UUIDUtil.getUUIDNumberString();
                HashMap<String, String> accessMap = new HashMap<>();
                accessMap.put("clientId", clientId);
                accessMap.put("openId", openId.toString());
                accessMap.put("randomAkId", randomAkId);
                String accessToken = JwtTokenUtil.generateToken(JSON.toJSONString(accessMap), expiresIn);


                HashMap<String, String> refreshMap = new HashMap<>();
                refreshMap.put("clientId", clientId);
                refreshMap.put("openId", openId.toString());
                refreshMap.put("dateTime", String.valueOf(System.currentTimeMillis()));
                String refreshToken = JwtTokenUtil.generateToken(JSON.toJSONString(refreshMap), refreshTokenExpiresIn);

                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                CookieUtil.set(response, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY, accessToken);

                TokenCache.putAK(accessToken, clientId, openId.toString(), randomAkId, expiresIn);

                TokenCache.putRK(refreshToken, clientId, openId.toString(), refreshTokenExpiresIn);
            } catch (Exception e) {
                throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_SERIALIZE_ERROR, e).errThrow();
            }

        }
        return function;
    }
}
