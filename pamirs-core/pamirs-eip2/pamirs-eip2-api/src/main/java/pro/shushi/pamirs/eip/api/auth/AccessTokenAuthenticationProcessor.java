package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.config.PamirsEipOpenApiProperties;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.converter.OpenApiGetAccessTokenConverter;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Optional;

@Slf4j
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Component
@Conditional(EipOpenApiSwitchCondition.class)
public class AccessTokenAuthenticationProcessor extends AbstractOpenApiAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    private static final long DEFAULT_DELAY_EXPIRES = 300;

    @Autowired
    private PamirsEipOpenApiProperties openApiConfiguration;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Function.fun(EipFunctionConstant.DEFAULT_TOKEN_AUTHENTICATION_PROCESSOR_FUN)
    @Function.Advanced(displayName = "开放接口Token有效性认证")
    @Function(name = EipFunctionConstant.DEFAULT_TOKEN_AUTHENTICATION_PROCESSOR_FUN)
    public Boolean authenticationFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return authentication(context, exchange);
    }

    /**
     * 1、获取accessToken，验证是否存在
     * 2、解密accessToken，获取appKey
     * 3、查看Redis中Token是否存在
     *
     * @param context  执行器上下文
     * @param exchange 交换信息
     * @return 认证结果
     */
    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String token = fetchString(context, exchange, OpenApiConstant.ACCESS_TOKEN_KEY, "300001", "无效的AccessToken");
        if (token == null) {
            return false;
        }
        String appKey;
        long timestamp;
        try {
            String decryptToken = EncryptHelper.decryptByKey(EncryptHelper.getSecretKeySpec(EncryptTypeEnum.AES.getValue(), openApiConfiguration.getRoute().getAesKey()), token);
            appKey = decryptToken.substring(0, 32);
            timestamp = Long.parseLong(decryptToken.substring(32));
        } catch (Exception e) {
            log.error("Unable to parse AccessToken", e);
            error(exchange, "300002", "无法解析的AccessToken");
            return false;
        }
        String currentToken = redisTemplate.opsForValue().get(OpenApiGetAccessTokenConverter.getAuthCacheKey(appKey));
        if (isInvalidToken(token, timestamp, currentToken)) {
            error(exchange, "300003", "失效的AccessToken");
            return false;
        }
        context.putExecutorContextValue(OpenApiConstant.OPEN_API_APP_KEY_KEY, appKey);
        return true;
    }

    private boolean isInvalidToken(String token, long timestamp, String currentToken) {
        if (currentToken == null || !currentToken.equals(token)) {
            return System.currentTimeMillis() - timestamp >= getDelayExpires();
        }
        return false;
    }

    private long getDelayExpires() {
        return Optional.ofNullable(openApiConfiguration.getRoute().getDelayExpires()).filter(v -> v.compareTo(1L) >= 0).orElse(DEFAULT_DELAY_EXPIRES) * 1000L;
    }
}
