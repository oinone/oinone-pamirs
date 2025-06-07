package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipAuthentication;
import pro.shushi.pamirs.eip.api.serializable.DefaultJSONSerializable;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;

@Slf4j
@Component
@Conditional(EipOpenApiSwitchCondition.class)
public class OpenApiDataDecryptProcessor extends AbstractOpenApiAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @SuppressWarnings("unchecked")
    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        EipApplication application = (EipApplication) context.getExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY);
        String appKey = application.getAppKey();
        EipAuthentication authentication = (EipAuthentication) context.getExecutorContextValue(OpenApiConstant.OPEN_API_EIP_AUTHENTICATION_KEY);
        EncryptTypeEnum encryptType = authentication.getEncryptType();
        String data;
        SuperMap interfaceContext = context.getInterfaceContext();
        int size = interfaceContext.size();
        if (size == 0) {
            return true;
        } else if (size == 1) {
            if (!interfaceContext.containsKey(EipContextConstant.RESULT_KEY)) {
                error(exchange, "500003", "数据结构有误，无法进行认证");
                return false;
            }
            data = interfaceContext.getString(EipContextConstant.RESULT_KEY);
        } else {
            error(exchange, "500004", "数据结构有误，无法进行认证");
            return false;
        }
        try {
            switch (encryptType) {
                case RSA:
                    data = EncryptHelper.decryptByKey(EncryptHelper.getPrivateKey(encryptType.getValue(), authentication.getPrivateKey()), data);
                    break;
                case AES:
                    data = EncryptHelper.decryptByKey(EncryptHelper.getSecretKeySpec(encryptType.getValue(), authentication.getPrivateKey()), data);
                    break;
                default:
                    error(exchange, "500001", String.format("无法识别的加密类型 [AppKey %s]", appKey));
                    return false;
            }
        } catch (Exception e) {
            error(exchange, "500002", "无法解析的传入数据");
            log.error("开放接口数据加密异常", e);
            return false;
        }
        IEipOpenInterface<SuperMap> eipOpenApi = (IEipOpenInterface<SuperMap>) context.getApi();
        context = eipOpenApi.getContextSupplier().get(eipOpenApi, context.getExecutorContext(), CommonApiFactory.getApi(DefaultJSONSerializable.class).serializable(data));
        EipInterfaceContext.setExecutorContext(exchange, context);
        return true;
    }
}
