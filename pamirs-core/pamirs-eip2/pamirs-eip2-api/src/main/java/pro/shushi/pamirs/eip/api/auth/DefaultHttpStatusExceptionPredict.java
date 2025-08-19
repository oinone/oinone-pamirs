package pro.shushi.pamirs.eip.api.auth;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipExceptionPredict;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import static pro.shushi.pamirs.eip.api.constant.EipFunctionConstant.EXCEPTION_PREDICT_PREFIX;

/**
 * DefaultHttpStatusExceptionPredict
 *
 * @author yakir on 2023/05/20 15:15.
 */
@Component
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class DefaultHttpStatusExceptionPredict implements IEipExceptionPredict<SuperMap> {

    public static final String HTTP_STATUS_EXP_FN = EXCEPTION_PREDICT_PREFIX + "defaultHttpStatusExp";

    @Function.fun(HTTP_STATUS_EXP_FN)
    @Function.Advanced(displayName = "Http响应状态异常判定")
    @Function(name = HTTP_STATUS_EXP_FN)
    public Boolean testFunction(IEipContext<SuperMap> context) {
        return test(context);
    }

    @Override
    public boolean test(IEipContext<SuperMap> context) {
        return null != context.getInterfaceContextValue("http.headers.status");
    }
}
