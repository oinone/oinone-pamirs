package pro.shushi.pamirs.eip.api.auth;

import org.apache.camel.ExtendedExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Component
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Conditional(EipOpenApiSwitchCondition.class)
public class OpenApiAuthenticationProcessor implements IEipAuthenticationProcessor<SuperMap> {

    @Autowired
    private AccessTokenAuthenticationProcessor accessTokenAuthenticationProcessor;

    @Autowired
    private EipApplicationAuthenticationProcessor eipApplicationAuthenticationProcessor;

    @Autowired
    private OpenApiDataDecryptProcessor openApiDataDecryptProcessor;

    @Autowired
    private OpenApiPermissionProcessor openApiPermissionProcessor;

    @Function.fun(EipFunctionConstant.DEFAULT_AUTHENTICATION_PROCESSOR_FUN)
    @Function.Advanced(displayName = "开放接口加密请求认证")
    @Function(name = EipFunctionConstant.DEFAULT_AUTHENTICATION_PROCESSOR_FUN)
    public Boolean authenticationFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return authentication(context, exchange);
    }

    /**
     * <h>认证策略综述</h>
     * <p>整个开放接口认证共分为四步：
     * 1、租户信息认证
     * 2、AccessToken认证
     * 3、集成应用认证
     * 4、数据解密并序列化为JSON格式
     * </p>
     * <p>
     * 1、获取accessToken，验证是否存在
     * 2、解密accessToken，获取appKey
     * 3、查看Redis中Token是否存在
     * 4、根据appKey查询集成应用是否存在
     * 5、判断当前环境的租户信息是否与集成应用的信息一致
     * 6、获取集成应用认证信息
     * 7、若请求数据存在，则解密请求数据，否则跳过
     * 8、刷新接口上下文
     * </p>
     *
     * @param context  执行器上下文
     * @param exchange 交换信息
     * @return 认证结果
     */
    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        if (!accessTokenAuthenticationProcessor.authentication(context, exchange)) {
            return false;
        }

        if (!eipApplicationAuthenticationProcessor.authentication(context, exchange)) {
            return false;
        }

        if (!openApiPermissionProcessor.authentication(context, exchange)) {
            return false;
        }

        if (!openApiDataDecryptProcessor.authentication(context, exchange)) {
            return false;
        }

        return true;
    }
}
