package pro.shushi.pamirs.eip.api.handler;

import org.apache.camel.Exchange;
import org.apache.camel.processor.ErrorHandler;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipExceptionHandler;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.util.EipLogUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.List;

@Slf4j
public class DefaultIntegrationInterfaceErrorHandler implements ErrorHandler {

    @Override
    public void process(Exchange exchange) throws Exception {
        Throwable e = exchange.getException();
        if (e == null) {
            e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        }
        EipResult<?> result;
        IEipContext<?> context = EipInterfaceContext.getExecutorContext(exchange);
        if (e == null) {
            result = EipResult.error(context, "Oops!", "无法处理的异常", null);
        } else {
            log.error("集成接口调用异常", e);
            if (e instanceof PamirsException) {
                PamirsException exception = (PamirsException) e;
                result = EipResult.error(context, StringHelper.valueOf(exception.getCode()), exception.getMessage(), exception);
            } else {
                result = EipResult.error(context, "Oops!", e.getMessage(), e);
            }
        }

        List<IEipExceptionHandler> handlers = BeanDefinitionUtils.getBeansOfTypeByOrdered(IEipExceptionHandler.class);
        EipResult<?> finalResult = null;
        for (IEipExceptionHandler handler : handlers) {
            if (handler.match(result, e)) {
                finalResult = handler.handler(result, e);
                if (finalResult != null) {
                    break;
                }
            }
        }
        if (finalResult == null) {
            finalResult = result;
        }

        exchange.getMessage().setBody(finalResult);

        EipLog eipLog = EipLogUtil.getEipLog(context);
        if (eipLog != null) {
            EipLogUtil.failure(context, eipLog, exchange);
        }
    }
}
