package pro.shushi.pamirs.eip.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.Pair;
import org.apache.camel.util.URISupport;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.constant.EipCharacterConstant;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.ParamProcessorTypeEnum;
import pro.shushi.pamirs.eip.api.model.*;
import pro.shushi.pamirs.eip.api.service.EipInterfaceService;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.*;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

public class EipHelper {

    public static String generatorDirectUri(String interfaceName) {
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        return "direct://" + keyPrefix + interfaceName;
    }

    public static String getStringJSONString(Map<String, Object> map) {
        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                stringMap.put(entry.getKey(), (String) value);
            }
        }
        return JSON.toJSONString(stringMap);
    }

    public static String getOpenInterfaceDomain(String path) {
        return getOpenInterfaceDomain(path, OpenApiConstant.OPEN_API_FIXED_PREFIX);
    }

    public static String getOpenInterfaceDomain(String path, String prefix) {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(prefix)) {
            return null;
        }
        if (prefix.charAt(0) != '/') {
            prefix = "/" + prefix;
        }
        return path.substring(0, path.indexOf(prefix));
    }

    public static String getStringBody(Exchange exchange) {
        Object body = exchange.getMessage().getBody();
        String stringBody = null;
        if (body instanceof String) {
            stringBody = (String) body;
        } else {
            if (body != null) {
                stringBody = JSON.toJSONString(body, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
            }
        }
        return stringBody;
    }

    /**
     * <h>根据Camel中RouteDefinitionId获取接口类型</h>
     * <p>
     * 以下判断的正确性取决于camel上下文中所有的路由定义全部都是通过以下方式进行注册的
     * {@link EipInterfaceContext#putInterface(IEipIntegrationInterface)}
     * {@link EipInterfaceService}
     * {@link EipInitializationUtil}
     * </p>
     *
     * @param routeDefinitionId {@link RouteDefinition#getId()}
     * @return 接口类型
     */
    public static InterfaceTypeEnum getInterfaceType(String routeDefinitionId) {
        if (routeDefinitionId.startsWith(EipInitializationUtil.INTEGRATION_API_ID_PREFIX)) {
            return InterfaceTypeEnum.INTEGRATION;
        }
        if (routeDefinitionId.startsWith(EipInitializationUtil.OPEN_API_ID_PREFIX)) {
            return InterfaceTypeEnum.OPEN;
        }
        if (EipInterfaceContext.getInterface(routeDefinitionId) == null) {
            if (EipInterfaceContext.getTemporaryInterface(routeDefinitionId) == null) {
                //当该接口不在上下文中定义时，则说明是开放接口。
                return InterfaceTypeEnum.OPEN;
            } else {
                //当该接口为临时接口时，则说明是路由定义
                return InterfaceTypeEnum.ROUTE;
            }
        } else {
            //当该接口为真实接口时，则说明是集成接口
            return InterfaceTypeEnum.INTEGRATION;
        }
    }

    public static void fillEipIntegrationInterface(EipIntegrationInterface integrationInterface) {
        EipParamProcessor requestParamProcessor = integrationInterface.getRequestParamProcessor();
        if (requestParamProcessor == null) {
            requestParamProcessor = new EipParamProcessor().setType(ParamProcessorTypeEnum.REQUEST);
            integrationInterface.setRequestParamProcessor(requestParamProcessor);
        }
        requestParamProcessor.setIntegrationInterface(integrationInterface);

        EipParamProcessor responseParamProcessor = integrationInterface.getResponseParamProcessor();
        if (responseParamProcessor == null) {
            responseParamProcessor = new EipParamProcessor().setType(ParamProcessorTypeEnum.RESPONSE);
            integrationInterface.setResponseParamProcessor(responseParamProcessor);
        }
        responseParamProcessor.setIntegrationInterface(integrationInterface);

        EipExceptionParamProcessor exceptionParamProcessor = integrationInterface.getExceptionParamProcessor();
        if (exceptionParamProcessor == null) {
            exceptionParamProcessor = new EipExceptionParamProcessor();
            integrationInterface.setExceptionParamProcessor(exceptionParamProcessor);
        }
        exceptionParamProcessor.setIntegrationInterface(integrationInterface);

        Optional.ofNullable(integrationInterface.getIncrementalProcessor()).ifPresent(v -> v.setIntegrationInterface(integrationInterface));
    }

    public static void fillEipOpenInterface(EipOpenInterface openInterface) {
        EipOpenParamProcessor requestParamProcessor = openInterface.getRequestParamProcessor();
        if (requestParamProcessor == null) {
            requestParamProcessor = new EipOpenParamProcessor();
            openInterface.setRequestParamProcessor(requestParamProcessor);
        }

        EipOpenParamProcessor responseParamProcessor = openInterface.getResponseParamProcessor();
        if (responseParamProcessor == null) {
            responseParamProcessor = new EipOpenParamProcessor();
            openInterface.setResponseParamProcessor(responseParamProcessor);
        }

        if (StringUtils.isBlank(openInterface.getFinalResultKey())) {
            openInterface.setFinalResultKey(EipContextConstant.RESULT_KEY);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void paramConvert(IEipContext<T> context, IEipParamConverterProcessor<T> paramConverterProcessor, ExtendedExchange exchange) {
        IEipConverter<T> converter = (IEipConverter<T>) context.getExecutorContextValue(IEipContext.REQUEST_CONVERT_PREFIX + context.getApi().getInterfaceName());
        if (converter == null) {
            converter = paramConverterProcessor.getConverter();
        }
        IEipParamConverter<T> paramConverter = (IEipParamConverter<T>) context.getExecutorContextValue(IEipContext.REQUEST_PARAM_CONVERT_PREFIX + context.getApi().getInterfaceName());
        if (paramConverter == null) {
            paramConverter = paramConverterProcessor.getParamConverter();
        }
        List<IEipConvertParam<T>> convertParamList = paramConverterProcessor.getConvertParamList();
        if (converter == null && paramConverter == null) {
            return;
        }
        if (converter != null) {
            converter.convert(context, exchange);
        }
        if (paramConverter != null && CollectionUtils.isNotEmpty(convertParamList)) {
            paramConverter.convert(context, convertParamList, paramConverterProcessor.getParamConverterCallback());
        }
    }

    public static <T> void incrementalProcess(IEipContext<T> context, IEipIncrementalProcessor<T> incrementalProcessor, ExtendedExchange exchange) {
        IEipConverter<T> converter = incrementalProcessor.getConverter();
        IEipIncrementalParamConverter<T> paramConverter = incrementalProcessor.getIncrementalParamConverter();
        List<IEipIncrementalParam> convertParamList = incrementalProcessor.getIncrementalParamList((String) context.getExecutorContextValue(EipContextConstant.INCREMENTAL_TAGS_KEY));
        if (converter == null && paramConverter == null) {
            return;
        }
        if (converter != null) {
            converter.convert(context, exchange);
        }
        if (paramConverter != null && CollectionUtils.isNotEmpty(convertParamList)) {
            paramConverter.convert(context, convertParamList, incrementalProcessor.getIncrementalParamConverterCallback());
        }
    }

    public static String getIntegrationInterfaceBasePath(String schema, String domain) {
        return schema + EipCharacterConstant.PROTOCOL_HOST_SEPARATOR + URLHelper.repairRelativePath(domain);
    }

    public static String getCurrentInterfaceName(String interfaceName) {
        return PamirsTenantSession.getTenant() + CharacterConstants.SEPARATOR_UNDERLINE + interfaceName;
    }

    public static String concatKeys(String baseKey, String... keys) {
        return StringHelper.concat(EipCharacterConstant.PARAMETER_PARAMETER_SEPARATOR, baseKey, keys);
    }

    public static String getPagingSizeInParam(String interfaceName) {
        return IEipContext.PAGING_PREFIX + interfaceName + IEipContext.PAGING_SIZE_SUFFIX;
    }

    public static String getPagingOffsetInParam(String interfaceName) {
        return IEipContext.PAGING_PREFIX + interfaceName + IEipContext.PAGING_OFFSET_SUFFIX;
    }

    public static String[] safeSplitRaw(String s) {
        List<String> list = new ArrayList<>();

        if (!s.contains("+")) {
            // no plus sign so there is only one part, so no need to split
            list.add(s);
        } else {
            // there is a plus sign so we need to split in a safe manner
            List<Pair<Integer>> rawPairs = URISupport.scanRaw(s);
            StringBuilder sb = new StringBuilder();
            char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char ch = chars[i];
                if (ch != '+' || URISupport.isRaw(i, rawPairs)) {
                    sb.append(ch);
                } else {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            }
            // any leftover?
            if (sb.length() > 0) {
                list.add(sb.toString());
                sb.setLength(0);
            }
        }

        return list.toArray(new String[list.size()]);
    }
}
