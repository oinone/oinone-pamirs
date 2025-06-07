package pro.shushi.pamirs.eip.view.action;

import com.alibaba.fastjson.JSON;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.model.EipIncrementalParam;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipInterfaceTestTransient;
import pro.shushi.pamirs.eip.api.model.EipParamProcessor;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Model.model(EipInterfaceTestTransient.MODEL_MODEL)
public class EipInterfaceTestTransientAction {

    @Function(openLevel = FunctionOpenEnum.API, summary = "集成接口测试构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
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
                .setExecuteContextData("{\n\t\n}")
                .setRequestData("{\n\t\n}")
                .setResponseData("");
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "集成接口下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public EipInterfaceTestTransient constructMirror(EipInterfaceTestTransient data) {
        EipIntegrationInterface integrationInterface = EipIntegrationInterfaceAction.fetchIntegrationInterface(data.getIntegrationInterface());
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Action(displayName = "模拟参数转换", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.FORM})
    public EipInterfaceTestTransient mockParamConverter(EipInterfaceTestTransient data) {
        IEipIntegrationInterface integrationInterface = fetchIntegrationInterface(data);
        IEipContext context = mockParamSerializable(data, integrationInterface);
        ExtendedExchange extendedExchange = (ExtendedExchange) ExchangeBuilder.anExchange(EipCamelContext.getContext().getCamelContext())
                .withProperty(EipContextConstant.CONTEXT_KEY, context)
                .withPattern(integrationInterface.getExchangePattern().getExchangePattern())
                .withBody(context.getInterfaceContext())
                .build();
        EipHelper.paramConvert(context, integrationInterface.getRequestParamProcessor(), extendedExchange);
        return data.setActualRequestData(JSON.toJSONString(EipInterfaceContext.getExecutorContext(extendedExchange).getInterfaceContext()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Action(displayName = "测试", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.FORM})
    public EipInterfaceTestTransient test(EipInterfaceTestTransient data) {
        IEipIntegrationInterface<?> integrationInterface = fetchIntegrationInterface(data);
        IEipContext<?> context = mockParamSerializable(data, integrationInterface);
        EipResult<?> result = EipInterfaceContext.call((IEipIntegrationInterface) integrationInterface, context.getExecutorContext(), context.getInterfaceContext());
        if (result.getSuccess()) {
            data.setTip("调用成功");
        } else {
            data.setTip("调用失败: " + result.getErrorCode() + " - " + result.getErrorMessage());
        }
        return data.setResponseData(result.getResult(String.class));
    }

    public IEipIntegrationInterface<?> fetchIntegrationInterface(EipInterfaceTestTransient data) {
        Boolean isDevelopment = data.getIsDevelopment();
        if (isDevelopment == null) {
            isDevelopment = Boolean.FALSE;
        }
        IEipIntegrationInterface<?> integrationInterface;
        if (!isDevelopment) {
            integrationInterface = EipIntegrationInterfaceAction.fetchIntegrationInterface(data.getIntegrationInterface());
            integrationInterface = EipInterfaceContext.getAnyInterface(integrationInterface.getInterfaceName());
        } else {
            String interfaceName = data.getInterfaceName();
            if (StringUtils.isBlank(interfaceName)) {
                integrationInterface = EipIntegrationInterfaceAction.fetchIntegrationInterface(data.getIntegrationInterface());
                integrationInterface = EipInterfaceContext.getAnyInterface(integrationInterface.getInterfaceName());
            } else {
                integrationInterface = EipInterfaceContext.getAnyInterface(interfaceName);
                if (integrationInterface == null) {
                    throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
                }
            }
        }
        return integrationInterface;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private IEipContext<?> mockParamSerializable(EipInterfaceTestTransient data, IEipIntegrationInterface integrationInterface) {
        String executeContextData = data.getExecuteContextData();
        Object executeContext = null;
        if (StringUtils.isNotBlank(executeContextData)) {
            executeContext = EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE.serializable(executeContextData);
        }
        String requestData = data.getRequestData();
        Object requestBody = null;
        if (StringUtils.isNotBlank(requestData)) {
            IEipParamProcessor<?> paramProcessor = integrationInterface.getRequestParamProcessor();
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
        }
        return integrationInterface.getContextSupplier().get(integrationInterface, executeContext, requestBody);
    }

    private void appendRequestParams(StringBuilder sb, IEipConvertParam<SuperMap> convertParam) {
        if (convertParam.getRequired()) {
            sb.append("(必填) ");
        } else {
            sb.append("         ");
        }
        sb.append("入参: ");
        String inParam = convertParam.getInParam();
        if (StringUtils.isBlank(inParam)) {
            sb.append("无");
        } else {
            sb.append(inParam);
        }
        sb.append(" 出参: ").append(convertParam.getOutParam());
        Object defaultValue = convertParam.getDefaultValue();
        if (ObjectHelper.isNotBlank(defaultValue)) {
            sb.append(" 默认值: ").append(StringHelper.valueOf(defaultValue));
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
}
