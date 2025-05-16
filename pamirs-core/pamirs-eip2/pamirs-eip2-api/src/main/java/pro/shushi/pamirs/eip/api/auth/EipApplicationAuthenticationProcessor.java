package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipAuthentication;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

/**
 * Eip开放接口集成应用有效性认证
 *
 * @author Adamancy Zhang at 18:23 on 2021-06-09
 */
@Slf4j
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Component
@Conditional(EipOpenApiSwitchCondition.class)
public class EipApplicationAuthenticationProcessor extends AbstractOpenApiAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Function.fun(EipFunctionConstant.DEFAULT_APPLICATION_AUTHENTICATION_PROCESSOR_FUN)
    @Function.Advanced(displayName = "开放接口集成应用有效性认证")
    @Function(name = EipFunctionConstant.DEFAULT_APPLICATION_AUTHENTICATION_PROCESSOR_FUN)
    public Boolean authenticationFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return authentication(context, exchange);
    }

    /**
     * * 认证策略
     * 1、获取accessToken，验证是否存在
     * 2、解密accessToken，获取appKey
     * 3、查看Redis中Token是否存在
     * 4、根据appKey查询集成应用是否存在
     * 5、判断当前环境的租户信息是否与集成应用的信息一致
     *
     * @param context  执行器上下文
     * @param exchange 交换信息
     * @return 认证结果
     */
    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String appKey = (String) context.getExecutorContextValue(OpenApiConstant.OPEN_API_APP_KEY_KEY);
        if (StringUtils.isBlank(appKey)) {
            error(exchange, "400001", "无法获取AppKey");
            return false;
        }
        EipApplication eipApplication = (EipApplication) exchange.getProperties().get(OpenApiConstant.EIP_APPLICATION_KEY);
        if (eipApplication == null) {
            eipApplication = new EipApplication().setAppKey(appKey).queryOne();
        }
        if (eipApplication == null) {
            error(exchange, "400002", String.format("不存在的集成应用 [AppKey %s]", appKey));
            return false;
        }
        if(!DataStatusEnum.ENABLED.equals(eipApplication.getDataStatus())){
            error(exchange, "400003", String.format("集成应用状态异常 [AppKey %s]", appKey));
            return false;
        }
        EipAuthentication authentication = eipApplication.fieldQuery(EipApplication::getAuthentication).getAuthentication();
        if (authentication == null) {
            error(exchange, "400004", String.format("集成应用无认证信息 [AppKey %s]", appKey));
            return false;
        }
        EncryptTypeEnum encryptType = authentication.getEncryptType();
        if (encryptType == null) {
            error(exchange, "400005", String.format("无法识别的加密类型 [AppKey %s]", appKey));
            return false;
        }
        context.putExecutorContextValue(OpenApiConstant.OPEN_API_EIP_APPLICATION_KEY, eipApplication);
        context.putExecutorContextValue(OpenApiConstant.OPEN_API_EIP_AUTHENTICATION_KEY, authentication);
        return true;
    }
}
