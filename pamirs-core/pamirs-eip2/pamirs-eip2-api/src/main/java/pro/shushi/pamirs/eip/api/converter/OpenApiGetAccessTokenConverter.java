package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.config.PamirsEipOpenApiProperties;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipAuthentication;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 开放接口获取Token
 *
 * @author Adamancy Zhang at 18:44 on 2021-06-09
 */
@Slf4j
@Component
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Conditional(EipOpenApiSwitchCondition.class)
public class OpenApiGetAccessTokenConverter implements IEipConverter<SuperMap> {

    private static final long DEFAULT_EXPIRES = 7200L;

    @Autowired
    private PamirsEipOpenApiProperties openApiConfiguration;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Function.fun(EipFunctionConstant.DEFAULT_OPEN_API_GET_ACCESS_TOKEN_FUN)
    @Function.Advanced(displayName = "开放接口获取Token")
    @Function(name = EipFunctionConstant.DEFAULT_OPEN_API_GET_ACCESS_TOKEN_FUN)
    public Object convertFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        convert(context, exchange);
        return null;
    }

    @Override
    public void convert(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String appKey = fetchString(context, exchange, OpenApiConstant.APP_KEY_KEY, "100001", "属性appKey不能为空");
        if (appKey == null) {
            return;
        }
        if (!verificationAppSecret(context, exchange, appKey)) {
            return;
        }
        String cacheKey = getAuthCacheKey(appKey);
        long expires = getExpires();
        Map<String, Object> result = new HashMap<>();
        String token = generatorToken(context, appKey);
        if (token == null) {
            return;
        }
        redisTemplate.opsForValue().set(cacheKey, token, expires, TimeUnit.SECONDS);
        result.put("access_token", token);
        result.put("expires_time", expires);
        success(context, result);
    }

    /**
     * 认证策略
     * 1、必须在请求参数或请求头中包含appKey
     * 2、必须在请求参数或请求头中包含appSecret
     * 3、根据appKey查询集成应用是否存在
     * 4、获取集成应用认证信息，解密appSecret，判断其是否与传入的appKey一致
     * 5、判断当前环境的租户信息是否与集成应用的信息一致
     * 6、从Redis获取已存在Token，若成功获取，则返回；否则，生成Token，并返回
     *
     * @param context  执行器上下文
     * @param exchange 交换信息
     * @param appKey   集成应用唯一标识
     */
    private boolean verificationAppSecret(IEipContext<SuperMap> context, ExtendedExchange exchange, String appKey) {
        String appSecret = fetchString(context, exchange, OpenApiConstant.APP_SECRET_KEY, "100002", "属性appSecret不能为空");
        if (appSecret == null) {
            return false;
        }
        EipApplication eipApplication = new EipApplication().setAppKey(appKey).queryOne();
        if (eipApplication == null) {
            error(context, "200001", String.format("不存在的集成应用 [AppKey %s]", appKey));
            return false;
        }
        if (!DataStatusEnum.ENABLED.equals(eipApplication.getDataStatus())) {
            error(context, "200002", String.format("集成应用状态异常 [AppKey %s]", appKey));
            return false;
        }
        EipAuthentication eipAuthentication = eipApplication.fieldQuery(EipApplication::getAuthentication).getAuthentication();
        if (eipAuthentication == null) {
            error(context, "200003", String.format("集成应用无认证信息 [AppKey %s]", appKey));
            return false;
        }
        EncryptTypeEnum encryptType = eipAuthentication.getEncryptType();
        if (encryptType == null) {
            error(context, "200004", String.format("无法识别的加密类型 [AppKey %s]", appKey));
            return false;
        }
        try {
            Key privateKey;
            switch (encryptType) {
                case RSA:
                    privateKey = EncryptHelper.getPrivateKey(encryptType.getValue(), eipAuthentication.getPrivateKey());
                    break;
                case AES:
                    privateKey = EncryptHelper.getSecretKeySpec(encryptType.getValue(), eipAuthentication.getPrivateKey());
                    break;
                default:
                    error(context, "200005", String.format("无法识别的加密类型 [AppKey %s]", appKey));
                    return false;
            }
            if (!compareSecretValue(context, appKey, EncryptHelper.decryptByKey(privateKey, appSecret), EncryptHelper.decryptByKey(privateKey, eipAuthentication.getAppSecret()))) {
                return false;
            }
        } catch (Exception e) {
            error(context, "200006", "无法解析的AppSecret");
            log.error("无法解析的AppSecret", e);
            return false;
        }
        return true;
    }

    private String generatorToken(IEipContext<SuperMap> context, String appKey) {
        try {
            return EncryptHelper.encryptByKey(EncryptHelper.getSecretKeySpec(EncryptTypeEnum.AES.getValue(),
                    openApiConfiguration.getRoute().getAesKey()), appKey + System.currentTimeMillis());
        } catch (Exception e) {
            error(context, "200007", "服务器正忙，请稍后再试");
            log.error("Token获取失败", e);
            return null;
        }
    }

    private long getExpires() {
        return Optional.ofNullable(openApiConfiguration.getRoute().getExpires()).filter(v -> v.compareTo(1L) >= 0).orElse(DEFAULT_EXPIRES);
    }

    private boolean compareSecretValue(IEipContext<SuperMap> context, String appKey, String secretValue1, String secretValue2) {
        int length = appKey.length();
        SecretValue value1 = SecretValue.parse(secretValue1, length),
                value2 = SecretValue.parse(secretValue2, length);
        if (!appKey.equals(value1.appKey)) {
            error(context, "200008", "AppKey与AppSecret不匹配");
            return false;
        }
        if (value1.timestamp != value2.timestamp) {
            error(context, "200009", "AppSecret已过期");
            return false;
        }
        return true;
    }

    private String fetchString(IEipContext<SuperMap> context, ExtendedExchange exchange, String key, String errorCode, String errorMsg) {
        String value = StringHelper.valueOf(context.getInterfaceContextValue(key));
        if (StringUtils.isBlank(value)) {
            value = StringHelper.valueOf(exchange.getMessage().getHeader(key));
            if (StringUtils.isBlank(value)) {
                error(context, errorCode, errorMsg);
                return null;
            }
        }
        return value;
    }

    private void error(IEipContext<SuperMap> context, String errorCode, String errorMsg) {
        context.putInterfaceContextValue(EipContextConstant.RESULT_KEY, OpenEipResult.error(errorCode, errorMsg));
    }

    private void success(IEipContext<SuperMap> context, Map<String, Object> result) {
        context.putInterfaceContextValue(EipContextConstant.RESULT_KEY, MapHelper.newInstance(result)
                .put(OpenApiConstant.OPEN_API_SUCCESS_KEY, true)
                .put(OpenApiConstant.OPEN_API_ERROR_CODE_KEY, OpenApiConstant.OPEN_API_ERROR_CODE_SUCCESS_VALUE)
                .put(OpenApiConstant.OPEN_API_ERROR_MSG_KEY, OpenApiConstant.OPEN_API_ERROR_MSG_SUCCESS_VALUE)
                .build());
    }

    public static String getAuthCacheKey(String key) {
        String tenant = PamirsTenantSession.getTenant();
        if (StringUtils.isNotBlank(tenant)) {
            return OpenApiConstant.OPEN_API_AUTH_CACHE_PREFIX_KEY + tenant + CharacterConstants.SEPARATOR_COLON + key + OpenApiConstant.OPEN_API_AUTH_CACHE_SUFFIX_KEY;
        } else {
            return OpenApiConstant.OPEN_API_AUTH_CACHE_PREFIX_KEY + key + OpenApiConstant.OPEN_API_AUTH_CACHE_SUFFIX_KEY;
        }
    }

    private static class SecretValue {

        private final String appKey;

        private final long timestamp;

        private SecretValue(String appKey, long timestamp) {
            this.appKey = appKey;
            this.timestamp = timestamp;
        }

        private static SecretValue parse(String secretValue, int length) {
            return new SecretValue(secretValue.substring(0, length), getTimestamp(secretValue, length));
        }

        private static long getTimestamp(String secretValue, int length) {
            if (secretValue.length() == length) {
                return 0L;
            } else {
                return Long.parseLong(secretValue.substring(length));
            }
        }
    }
}
