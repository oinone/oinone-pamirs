package pro.shushi.pamirs.eip.api.util;

import org.apache.camel.Expression;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;
import pro.shushi.pamirs.eip.api.IEipFilter;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.model.EipComponentDefinition;
import pro.shushi.pamirs.eip.api.model.EipParamProcessor;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.processor.DefaultComponentProcessor;

import java.util.function.BiConsumer;

/**
 * @author drome
 * @date 2021/7/294:36 下午
 */
public abstract class EipCamelRouteBaseUtil {

    protected EipInitializationUtil initializationUtil;

    protected ProcessorDefinition processorDefinition;

    protected EipCamelRouteBaseUtil() {
    }

    public <T extends EipCamelRouteBaseUtil> T to(String interfaceName) {
        return to(EipInitializationUtil.getEipInterfaceByContext(interfaceName), Boolean.FALSE, EipInitializationUtil.TO_ROUTE_DEFINITION);
    }

    public <T extends EipCamelRouteBaseUtil> T to(IEipIntegrationInterface eipInterface) {
        return to(eipInterface, Boolean.TRUE, EipInitializationUtil.TO_ROUTE_DEFINITION);
    }

    public <T extends EipCamelRouteBaseUtil> T to(String interfaceName, BiConsumer<IEipIntegrationInterface, ProcessorDefinition<?>> consumer) {
        consumer.accept(EipInitializationUtil.getEipInterfaceByContext(interfaceName), processorDefinition);
        return (T) this;
    }

    public <T extends EipCamelRouteBaseUtil> T to(IEipIntegrationInterface eipInterface, BiConsumer<IEipIntegrationInterface, ProcessorDefinition<?>> consumer) {
        initializationUtil.temporaryRouteDefinitionProcessor(eipInterface);
        consumer.accept(eipInterface, processorDefinition);
        return (T) this;
    }

    public <T extends EipCamelRouteBaseUtil> T to(IEipIntegrationInterface eipInterface, Boolean isTemporary, BiConsumer<IEipIntegrationInterface, ProcessorDefinition<?>> consumer) {
        if (isTemporary) {
            initializationUtil.temporaryRouteDefinitionProcessor(eipInterface);
        }
        processorDefinition.process(eipInterface.getRequestParamProcessor().getProcessor());
        consumer.accept(eipInterface, processorDefinition);
        processorDefinition.process(eipInterface.getExceptionParamProcessor().getProcessor())
                .process(eipInterface.getResponseParamProcessor().getProcessor());
        //启用分页
        if (eipInterface.getPaging() != null) {
            processorDefinition.dynamicRouter(EipFunctionConstant.DEFAULT_PAGING_EXPRESSION);
        }
        return (T) this;
    }

    public EipCamelRouteFilterUtil filter(IEipFilter filter) {
        return EipCamelRouteFilterUtil.newInstatnce(this, filter);
    }

    public <T extends EipCamelRouteBaseUtil> T convert(IEipConverter converter) {
        processorDefinition.process(exchange -> {
            converter.convert(EipInterfaceContext.getExecutorContext(exchange), (ExtendedExchange) exchange);
        });
        return (T) this;
    }

    public <T extends EipCamelRouteBaseUtil> T paramProcessor(EipParamProcessor paramProcessor) {
        processorDefinition.process(exchange -> {

            IEipContext<SuperMap> context = EipInterfaceContext.getExecutorContext(exchange);
            Object body = exchange.getMessage().getBody();
            //序列化入参
            SuperMap interfaceContext = paramProcessor.getSerializable().serializable(body);

            //更新执行器上下文
            context = EipFunctionConstant.DEFAULT_CONTEXT_SUPPLIER.get(context.getApi(), context.getExecutorContext(), interfaceContext);
            EipInterfaceContext.setExecutorContext(exchange, context);

            //参数转换
            EipHelper.paramConvert(context, paramProcessor, (ExtendedExchange) exchange);

            //反序列化出参
            String finalResultKey = paramProcessor.getFinalResultKey();
            if (StringUtils.isBlank(finalResultKey)) {
                // TODO: 2021/8/9 这里取了最开始body转的对象,所以 EipHelper.paramConvert中的convertFun对body的修改不生效, 修改interfaceContext对象引用应该也会不生效
                //使用反序列化方式拿到最终结果
                body = paramProcessor.getDeserialization().deserialization(interfaceContext);
            } else {
                //根据指定最终结果的键值拿到出参结果
                body = context.getInterfaceContextValue(finalResultKey);
            }

            //最终出参转换
            body = paramProcessor.getInOutConverter().exchangeObject((ExtendedExchange) exchange, body);

            //设置出参
            exchange.getMessage().setBody(body);

        });
        return (T) this;
    }

    public <T extends EipCamelRouteBaseUtil> T component(EipRouteDefinition eipRouteDefinition, EipComponentDefinition eipComponentDefinition) {
        processorDefinition.process(new DefaultComponentProcessor(eipRouteDefinition, eipComponentDefinition));
        return (T) this;
    }

    @Deprecated
    public EipCamelRouteBaseUtil dynamicRouter(Expression expression) {
        processorDefinition.dynamicRouter(expression);
        return this;
    }
}
