package pro.shushi.pamirs.eip.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.constant.WebServicePrefix;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.model.EipIncrementalParam;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipInterfaceTestTransient;
import pro.shushi.pamirs.eip.api.model.EipParamProcessor;
import pro.shushi.pamirs.eip.api.service.EipInterfaceTestService;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;

/**
 * @author Adamancy Zhang at 18:10 on 2025-08-12
 */
@Slf4j
@Service
@Fun(EipInterfaceTestService.FUN_NAMESPACE)
public class EipInterfaceTestServiceImpl implements EipInterfaceTestService {

    @Function
    @Override
    public EipInterfaceTestTransient construct(EipInterfaceTestTransient data, EipIntegrationInterface integrationInterface) {
        if (data == null) {
            data = new EipInterfaceTestTransient();
        }
        if (integrationInterface != null) {
            integrationInterface = FetchUtil.fetchOneOfNullable(integrationInterface);
            if (integrationInterface != null) {
                data = constructMirror(data.setIntegrationInterface(integrationInterface));
            }
        }
        return data.setIsDevelopment(Boolean.FALSE)
                .setInterfaceName("")
                .setTip("请确认接口调用环境是否为可测试环境，否则可能带来不可挽回的后果")
                .setResponseData("");
    }

    private EipInterfaceTestTransient constructMirror(EipInterfaceTestTransient data) {
        EipIntegrationInterface integrationInterface = fetchIntegrationInterface(data.getIntegrationInterface());
        EipParamProcessor paramProcessor = integrationInterface.getRequestParamProcessor();
        List<IEipConvertParam<SuperMap>> convertParamList = paramProcessor.getConvertParamList();
        StringBuilder executorContextParamsBuilder = new StringBuilder();
        StringBuilder interfaceContextParamsBuilder = new StringBuilder();
//        StringBuilder incrementalParamsBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(convertParamList)) {
            for (IEipConvertParam<SuperMap> convertParam : convertParamList) {
                switch (convertParam.getOriginContextType()) {
                    case EXECUTOR:
                        appendRequestParams(executorContextParamsBuilder, convertParam);
                        break;
                    case INTERFACE:
                        appendRequestParams(interfaceContextParamsBuilder, convertParam);
                        break;
                }
            }
        }
//        List<EipIncrementalParam> incrementalParamList = (List<EipIncrementalParam>) (Object) Optional.ofNullable(integrationInterface.getIncrementalProcessor()).map(EipIncrementalProcessor::getIncrementalParamList).orElse(new ArrayList<>());
//        for (EipIncrementalParam incrementalParam : incrementalParamList) {
//            appendRequestParams(incrementalParamsBuilder, incrementalParam);
//        }
        StringBuilder finalRequestParamsBuilder = new StringBuilder();
        String finalResultKey = paramProcessor.getFinalResultKey();
        finalRequestParamsBuilder.append("最终请求出参键值: ");
        if (StringUtils.isBlank(finalResultKey)) {
            finalRequestParamsBuilder.append("全部");
        } else {
            finalRequestParamsBuilder.append(finalResultKey);
        }
        finalRequestParamsBuilder.append("\n");
        if (executorContextParamsBuilder.length() >= 1) {
            finalRequestParamsBuilder.append("上下文参数:\n").append(executorContextParamsBuilder.toString());
        }
        if (interfaceContextParamsBuilder.length() >= 1) {
            finalRequestParamsBuilder.append("请求参数:\n").append(interfaceContextParamsBuilder.toString());
        }
//        if (incrementalParamsBuilder.length() >= 1) {
//            finalRequestParamsBuilder.append("增量参数:\n").append(incrementalParamsBuilder.toString());
//        }
        return data.setTip("请确认接口调用环境是否为可测试环境，否则可能带来不可挽回的后果")
                .setInterfaceName("")
                .setResponseData("")
                .setRequestParams(finalRequestParamsBuilder.toString());
    }

    private void appendRequestParams(StringBuilder sb, IEipConvertParam<SuperMap> convertParam) {
        if (convertParam.getRequired()) {
            sb.append("(必填) ");
        } else {
            sb.append("           ");
        }
        sb.append("入参: ");
        String inParam = convertParam.getInParam();
        if (StringUtils.isBlank(inParam)) {
            sb.append("无");
        } else {
            sb.append(inParam);
        }
        sb.append("\n           出参: ").append(convertParam.getOutParam());
        Object defaultValue = convertParam.getDefaultValue();
        if (ObjectHelper.isNotBlank(defaultValue)) {
            sb.append("; 默认值: ").append(StringHelper.valueOf(defaultValue));
        }
        Boolean isKeepNull = convertParam.getIsKeepNull();
        if (isKeepNull != null) {
            sb.append("; 保留空值: ").append(isKeepNull);
        }
        if (ParamTypeEnum.ENUMERATION.equals(convertParam.getInParamType())) {
            for (Map.Entry<String, String> mapping : Optional.ofNullable(convertParam.getConvertMap()).map(Map::entrySet).orElse(new HashSet<>())) {
                sb.append("\n\t\t").append(mapping.getKey()).append(" -> ").append(mapping.getValue());
            }
        }
        sb.append("\n");
    }

    private void appendRequestParams(StringBuilder sb, EipIncrementalParam incrementalParam) {
        sb.append("         ").append("入参: ");
        String inParam = incrementalParam.getInParam();
        if (StringUtils.isBlank(inParam)) {
            sb.append("无");
        } else {
            sb.append(inParam);
        }
        sb.append(" 出参: ").append(incrementalParam.getOutParam())
                .append(" 初始值: ").append(incrementalParam.getInitializationValue())
                .append(" 当前值: ").append(incrementalParam.getCurrentValue())
                .append("\n");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Function
    @Override
    public EipInterfaceTestTransient mockParamConverter(EipInterfaceTestTransient data) {
        IEipIntegrationInterface integrationInterface = fetchIntegrationInterface(data);
        IEipIntegrationInterface actualIntegrationInterface = EipInterfaceContext.getInterface(integrationInterface.getInterfaceName());
        if (actualIntegrationInterface == null) {
            actualIntegrationInterface = integrationInterface;
        }
        IEipContext context = mockParamSerializable(data, actualIntegrationInterface);
        ExtendedExchange extendedExchange = (ExtendedExchange) ExchangeBuilder.anExchange(EipCamelContext.getContext().getCamelContext())
                .withProperty(EipContextConstant.CONTEXT_KEY, context)
                .withPattern(actualIntegrationInterface.getExchangePattern().getExchangePattern())
                .withBody(context.getInterfaceContext())
                .build();
        EipHelper.paramConvert(context, actualIntegrationInterface.getRequestParamProcessor(), extendedExchange);
        return data.setActualRequestData(toJSONString(EipInterfaceContext.getExecutorContext(extendedExchange).getInterfaceContext()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Function
    @Override
    public EipInterfaceTestTransient test(EipInterfaceTestTransient data) {
        IEipIntegrationInterface<?> integrationInterface = fetchIntegrationInterface(data);
        IEipContext<?> context = mockParamSerializable(data, integrationInterface);
        EipResult<?> result = EipInterfaceContext.call((IEipIntegrationInterface) integrationInterface, context.getExecutorContext(), context.getInterfaceContext());
        if (result.getSuccess()) {
            data.setTip("调用成功");
        } else {
            data.setTip("调用失败: " + result.getErrorCode() + " - " + result.getErrorMessage());
        }
        try {
            String responseData = result.getResult(String.class);
            if (StringUtils.isNotBlank(responseData) && JSON.isValid(responseData)) {
                data.setResponseData(toJSONString(JSON.parse(responseData)));
                return data;
            }
            return data.setResponseData(responseData);
        } catch (Throwable e) {
            String responseType = Optional.ofNullable(result.getResult())
                    .map(v -> v.getClass().getName())
                    .orElse(null);
            String message = String.format("Unsupported response data type: %s.", responseType);
            log.error(message, e);
            data.setResponseData(message);
            return data;
        }
    }

    public IEipIntegrationInterface<?> fetchIntegrationInterface(EipInterfaceTestTransient data) {
        Boolean isDevelopment = data.getIsDevelopment();
        if (isDevelopment == null) {
            isDevelopment = Boolean.FALSE;
        }
        IEipIntegrationInterface<?> integrationInterface;
        if (isDevelopment) {
            String interfaceName = data.getInterfaceName();
            if (StringUtils.isBlank(interfaceName)) {
                integrationInterface = fetchIntegrationInterface(data.getIntegrationInterface());
                integrationInterface = EipInterfaceContext.getAnyInterface(integrationInterface.getInterfaceName());
            } else {
                integrationInterface = EipInterfaceContext.getAnyInterface(interfaceName);
                if (integrationInterface == null) {
                    throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
                }
            }
        } else {
            integrationInterface = fetchIntegrationInterface(data.getIntegrationInterface());
            integrationInterface = EipInterfaceContext.getAnyInterface(integrationInterface.getInterfaceName());
            if (integrationInterface == null) {
                throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
            }
        }
        return integrationInterface;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private IEipContext<?> mockParamSerializable(EipInterfaceTestTransient data, IEipIntegrationInterface integrationInterface) {
        String executeContextData = data.getExecuteContextData();
        Object executeContext = null;
        if (StringUtils.isNotBlank(executeContextData)) {
            executeContext = EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE.serializable(executeContextData);
        }
        IEipIntegrationInterface actualIntegrationInterface = EipInterfaceContext.getInterface(integrationInterface.getInterfaceName());
        if (actualIntegrationInterface == null) {
            actualIntegrationInterface = integrationInterface;
        }
        String requestData = data.getRequestData();
        Object requestBody = null;
        if (StringUtils.isNotBlank(requestData)) {
            IEipParamProcessor<?> paramProcessor = actualIntegrationInterface.getRequestParamProcessor();
            if (paramProcessor == null) {
                throw PamirsException.construct(EipExpEnumerate.GET_REQUEST_PARAM_PROCESSOR_NULL_ERROR).errThrow();
            }
            IEipSerializable<?> serializable = paramProcessor.getSerializable();
            if (serializable == null) {
                throw PamirsException.construct(EipExpEnumerate.GET_SERIALIZABLE_NULL_ERROR).errThrow();
            }
            try {
                requestBody = serializable.serializable(requestData);
            } catch (Exception e) {
                throw PamirsException.construct(EipExpEnumerate.RESPONSE_PARAM_SERIALIZABLE_DATA_ERROR, e).errThrow();
            }
            String op = getWebServiceOp(paramProcessor);
            if (StringUtils.isNotBlank(op) && requestBody instanceof Map) {
                requestBody = ((Map<?, ?>) requestBody).get(op);
                if (requestBody instanceof Collection) {
                    SuperMap result = new SuperMap();
                    result.put(EipContextConstant.LIST_KEY, requestBody);
                    requestBody = result;
                }
            }
        }
        IEipContext<?> context = actualIntegrationInterface.getContextSupplier().get(actualIntegrationInterface, executeContext, requestBody);
        context.putAllInterfaceContextValue(EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE.serializable(data.getRequestQueryData()));
        context.putAllInterfaceContextValue(EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE.serializable(data.getRequestPathData()));
        context.putAllInterfaceContextValue(EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE.serializable(data.getRequestHeaderData()));
        return context;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String getWebServiceOp(IEipParamProcessor paramProcessor) {
        List<IEipConvertParam> convertParamList = paramProcessor.getConvertParamList();
        if (CollectionUtils.isEmpty(convertParamList)) {
            return null;
        }
        for (IEipConvertParam convertParam : convertParamList) {
            if (WebServicePrefix.PAMIRS_WEBSERVICE_OP.equals(convertParam.getInParam())) {
                return (String) convertParam.getDefaultValue();
            }
        }
        return null;
    }

    private EipIntegrationInterface fetchIntegrationInterface(EipIntegrationInterface data) {
        data = FetchUtil.fetchOne(data);
        if (data == null) {
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
        }
        return data;
    }

    private String toJSONString(Object object) {
        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
