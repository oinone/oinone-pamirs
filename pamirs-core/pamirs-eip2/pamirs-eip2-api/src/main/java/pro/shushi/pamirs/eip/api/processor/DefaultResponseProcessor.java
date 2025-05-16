package pro.shushi.pamirs.eip.api.processor;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.entry.InitializationBody;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.eip.api.util.EipLogUtil;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import java.util.List;

public class DefaultResponseProcessor extends AbstractEipIntegrationInterfaceProcessor<SuperMap> implements IEipProcessor<IEipIntegrationInterface<SuperMap>> {

    private final String pagingEnabledKey;

    private final String pagingSizeKey;

    private final String pagingStartPageKey;

    private final String pagingEndPageKey;

    private final String pagingCurrentPageKey;

    private final String pagingOffsetKey;

    public DefaultResponseProcessor(IEipIntegrationInterface<SuperMap> eipInterface) {
        super(eipInterface);
        this.pagingEnabledKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_ENABLED_SUFFIX;
        this.pagingSizeKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_SIZE_SUFFIX;
        this.pagingStartPageKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_START_PAGE_SUFFIX;
        this.pagingEndPageKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_END_PAGE_SUFFIX;
        this.pagingCurrentPageKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_CURRENT_PAGE_SUFFIX;
        this.pagingOffsetKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_OFFSET_SUFFIX;
    }

    @Override
    public void processor(ExtendedExchange exchange) throws Exception {
        IEipIntegrationInterface<SuperMap> integrationInterface = getApi();
        Message message = exchange.getMessage();
        Object body = message.getBody();

        //获取执行器上下文
        IEipContext<SuperMap> context = EipInterfaceContext.getExecutorContext(exchange);

        //获取响应参数处理器
        IEipParamProcessor<SuperMap> paramProcessor = integrationInterface.getResponseParamProcessor();

        //序列化入参
        SuperMap interfaceContext;
        if (body instanceof SuperMap) {
            interfaceContext = (SuperMap) body;
        } else {
            interfaceContext = paramProcessor.getSerializable().serializable(body);
        }

        //更新执行器上下文
        context = refreshExecutorContext(exchange, context, interfaceContext);

        //参数转换
        EipHelper.paramConvert(context, paramProcessor, exchange);

        //更新执行器上下文
        context = EipInterfaceContext.getExecutorContext(exchange);
        interfaceContext = context.getInterfaceContext();

        //反序列化出参
        String finalResultKey = paramProcessor.getFinalResultKey();
        if (StringUtils.isBlank(finalResultKey)) {
            //使用反序列化方式拿到最终结果
            body = paramProcessor.getDeserialization().deserialization(interfaceContext);
        } else {
            //根据指定最终结果的键值拿到出参结果
            body = context.getInterfaceContextValue(finalResultKey);
        }

        //响应结果处理
        body = responseResultProcessor(context, exchange, body);

        EipLog eipLog = EipLogUtil.getEipLog(context);

        //分页自动中断处理
        Boolean isNeedPaging = (Boolean) context.getExecutorContextValue(pagingEnabledKey);
        if (isNeedPaging != null && isNeedPaging && body instanceof List) {
            if (pagingProcessor(context, exchange, ((List<?>) body).size())) {
                //当需要分页时，对单次接口调用日志进行成功处理
                if (eipLog != null) {
                    EipLogUtil.success(context, eipLog);
                }
                return;
            }
        } else {
            context.putExecutorContextValue(pagingEnabledKey, false);
        }

        //最终出参转换
        body = paramProcessor.getInOutConverter().exchangeObject(exchange, body);

        //设置出参
        message.setBody(body);

        //成功响应，并成功处理
        if (eipLog != null) {
            EipLogUtil.success(context, eipLog);
        }
    }

    protected boolean pagingProcessor(IEipContext<SuperMap> context, ExtendedExchange exchange, Integer listSize) {
        IEipPaging<SuperMap> paging = getApi().getPaging();
        //获取基本分页参数
        Integer size = (Integer) context.getExecutorContextValue(pagingSizeKey);
        Integer endPage = (Integer) context.getExecutorContextValue(pagingEndPageKey);
        Integer currentPage = (Integer) context.getExecutorContextValue(pagingCurrentPageKey);
        Integer offset = (Integer) context.getExecutorContextValue(pagingOffsetKey);
        boolean isRequiredPredict = size == null,
                isNeedPageParameter = currentPage != null,
                isNeedInterrupt = isNeedPageParameter && endPage != null && endPage < 0,
                isContinueQuery;
        IEipPagingPredict<SuperMap> pagingPredict = paging.getPredict();
        if (isRequiredPredict) {
            if (pagingPredict == null) {
                throw PamirsException.construct(EipExpEnumerate.EIP_PROCESSOR_PAGESIZE_QUERY_ERROR).errThrow();
            }
            isContinueQuery = pagingPredict.predict(context, exchange);
        } else {
            if (listSize < size || (isNeedInterrupt && currentPage >= endPage)) {
                isContinueQuery = false;
            } else {
                isContinueQuery = true;
                if (pagingPredict != null) {
                    isContinueQuery = pagingPredict.predict(context, exchange);
                }
            }
        }
        context.putExecutorContextValue(pagingEnabledKey, isContinueQuery);
        if (isContinueQuery) {
            //调整当前偏移量
            context.putExecutorContextValue(pagingOffsetKey, offset + listSize);
            if (isNeedPageParameter) {
                //调整当前页数
                context.putExecutorContextValue(pagingCurrentPageKey, currentPage + 1);
            }
        }
        return isContinueQuery;
    }

    protected Object responseResultProcessor(IEipContext<SuperMap> context, ExtendedExchange exchange, Object body) {

        //类序列化标记
        boolean isSerializable = false;

        //单次幂等处理
        InitializationBody<String, Object> initializationBody = idempotent(context, exchange, body, isSerializable);
        body = initializationBody.getValue();
        isSerializable = initializationBody.isProcessed();

        //循环幂等处理
        if (body instanceof List) {
            initializationBody = idempotentEach(context, exchange, body, isSerializable);
            body = initializationBody.getValue();
            isSerializable = initializationBody.isProcessed();
        }

        //单次回调
        callback(context, exchange, body, isSerializable);

        //循环回调
        if (body instanceof List) {
            callbackEach(context, exchange, body, isSerializable);
        }

        return body;
    }
}
