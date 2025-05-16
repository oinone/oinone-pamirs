package pro.shushi.pamirs.eip.api.handler;

import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.camel.processor.ErrorHandler;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.util.EipLogUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

@Slf4j
public class DefaultOpenInterfaceErrorHandler implements ErrorHandler {

    @Override
    public void process(Exchange exchange) throws Exception {
        Throwable e = exchange.getException();
        if (e == null) {
            e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        }

        IEipContext<?> context = EipInterfaceContext.getExecutorContext(exchange);
        IEipApi eipApi = context.getApi();
        String interfaceName = null;
        String uri = null;
        if (eipApi != null) {
            interfaceName = eipApi.getInterfaceName();
            uri = eipApi.getUri();
        }

        String errorCode = "Oops!";
        String errorMsg;
        if (e == null) {
            log.error("OpenAPI has error. interfaceName: {}, uri: {}", interfaceName, uri);
            errorMsg = "服务器正忙，请稍后再试";
        } else {
            log.error("OpenAPI has error. interfaceName: {}, uri: {}", interfaceName, uri, e);
            if (e instanceof PamirsException) {
                PamirsException exception = (PamirsException) e;
                errorCode = StringHelper.valueOf(exception.getCode());
                errorMsg = exception.getMessage();
            } else {
                errorMsg = "开放接口出现无法预知的错误";
            }
        }
        String resultString = JSON.toJSONString(OpenEipResult.error(errorCode, errorMsg));

        exchange.getMessage().setBody(resultString);

        EipLog eipLog = EipLogUtil.getEipLog(context);
        if (eipLog != null) {
            EipLogUtil.failure(context, eipLog, exchange);
        }
    }
}
