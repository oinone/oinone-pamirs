package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipAuthenticationProcessor;
import pro.shushi.pamirs.eip.api.IEipContext;

/**
 * 实现平台Function机制
 */
public class DefaultAuthenticationProcessorFunction extends AbstractExecuteFunction implements IEipAuthenticationProcessor<SuperMap> {

    private final String signatureNamespace;

    private final String signatureFun;

    public DefaultAuthenticationProcessorFunction(String namespace, String fun) {
        this(namespace, fun, null, null);
    }

    public DefaultAuthenticationProcessorFunction(String namespace, String fun, String signatureNamespace, String signatureFun) {
        super(namespace, fun);
        if (StringUtils.isNotBlank(signatureNamespace)) {
            this.signatureNamespace = signatureNamespace;
        } else {
            this.signatureNamespace = null;
        }
        if (StringUtils.isNotBlank(signatureFun)) {
            this.signatureFun = signatureFun;
        } else {
            this.signatureFun = null;
        }
    }

    public String getSignatureNamespace() {
        return signatureNamespace;
    }

    public String getSignatureFun() {
        return signatureFun;
    }

    @Override
    public boolean authentication(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        Boolean result = (Boolean) ignoreHookCall(context, exchange);
        if (result == null) {
            return Boolean.TRUE;
        } else {
            return result;
        }
    }

    @Override
    public void signature(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        if (signatureNamespace != null && signatureFun != null) {
            ignoreHookCall(signatureNamespace, signatureFun, context, exchange);
        }
    }
}
