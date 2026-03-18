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
import pro.shushi.pamirs.locale.utils.I18nUtils;
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
                error(exchange, "500003", I18nUtils.getMessage("OpenApiDataDecryptProcessor.data_structure_error"));
                return false;
            }
            data = interfaceContext.getString(EipContextConstant.RESULT_KEY);
        } else {
            error(exchange, "500004", I18nUtils.getMessage("OpenApiDataDecryptProcessor.data_structure_error"));
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
                    error(exchange, "500001", I18nUtils.getMessage("OpenApiDataDecryptProcessor.encrypt_type_unknown", appKey));
                    return false;
            }
        } catch (Exception e) {
            error(exchange, "500002", I18nUtils.getMessage("OpenApiDataDecryptProcessor.parse_data_error"));
            log.error("Open interface data encryption exception", e);
            return false;
        }
        IEipOpenInterface<SuperMap> eipOpenApi = (IEipOpenInterface<SuperMap>) context.getApi();
        context = eipOpenApi.getContextSupplier().get(eipOpenApi, context.getExecutorContext(), CommonApiFactory.getApi(DefaultJSONSerializable.class).serializable(data));
        EipInterfaceContext.setExecutorContext(exchange, context);
        return true;
    }
}
