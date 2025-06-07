package pro.shushi.pamirs.eip.api.annotation;

import org.apache.camel.Exchange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.ParamProcessorTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipConvertParam;
import pro.shushi.pamirs.eip.api.model.EipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipParamProcessor;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EipIntegrateConvert {

    public static EipIntegrationInterface convert(Function function, Method method, IEipAnnotationSingletonConfig eipAnnotationSingletonConfig) {

        Integrate integrate = AnnotationUtils.findAnnotation(method, Integrate.class);
        if (integrate == null) {
            return null;
        }

        Integrate.Advanced integrateAdvanced = empty(AnnotationUtils.findAnnotation(method, Integrate.Advanced.class));
        Class<?> configClass = integrate.config();
        IEipAnnotationSingletonConfig eipConfigModel = null;
        try {
            eipConfigModel = (IEipAnnotationSingletonConfig) ((IEipAnnotationSingletonConfig) configClass.newInstance()).singletonModel();

            if (eipConfigModel == null) {
                //没有初始化
                return null;
            }
            if (eipAnnotationSingletonConfig != null && (!eipAnnotationSingletonConfig.getKey().equals(eipConfigModel.getKey()))) {
                return null;
            }
        } catch (InstantiationException e) {
            throw PamirsException.construct(EipExpEnumerate.SYSTEM_ERROR, e).errThrow();
        } catch (IllegalAccessException e) {
            throw PamirsException.construct(EipExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        Integrate.Advanced commonIntegrateAdvanced = empty(AnnotationUtils.findAnnotation(configClass, Integrate.Advanced.class));

        //schema\host的优先级，接口定义>配置>通用定义
        String schema = optional(integrateAdvanced.schema(), optional(eipConfigModel.getSchema(), commonIntegrateAdvanced.schema()));
        String host = optional(integrateAdvanced.host(), optional(eipConfigModel.getHost(), commonIntegrateAdvanced.host()));
        String path = optional(integrateAdvanced.path(), commonIntegrateAdvanced.path());
        String httpMethod = optional(integrateAdvanced.httpMethod(), optional(commonIntegrateAdvanced.httpMethod(), HttpMethod.POST.name()));

        EipIntegrationInterface eipIntegrationInterface = new EipIntegrationInterface();

        eipIntegrationInterface.setRequestParamProcessor(convertRequestProcessor(method, configClass, httpMethod));
        eipIntegrationInterface.setResponseParamProcessor(convertResponseParamProcessor(method, configClass));
        eipIntegrationInterface.setExceptionParamProcessor(convertExceptionParamProcessor(method, configClass));

        eipIntegrationInterface.setUri(schema + "://" + host + path);
        eipIntegrationInterface.setName(StringUtils.isNotBlank(integrate.name()) ? integrate.name() : function.getDisplayName());
        eipIntegrationInterface.setModule(function.getModule());
        eipIntegrationInterface.setInterfaceName(getInterfaceName(function.getNamespace(), function.getFun()));
        if (commonIntegrateAdvanced.dynamicProtocolCacheSize() >= 1) {
            eipIntegrationInterface.setIsDynamic(Boolean.TRUE);
            eipIntegrationInterface.setDynamicProtocolCacheSize(commonIntegrateAdvanced.dynamicProtocolCacheSize());
        }

        eipIntegrationInterface.construct();

        return eipIntegrationInterface;
    }

    public static String getInterfaceName(String namespace, String fun) {
        return namespace + "#" + fun;
    }

    private static EipExceptionParamProcessor convertExceptionParamProcessor(Method method, Class<?> configClass) {
        Integrate.ExceptionProcessor exceptionProcessorAnno = empty(AnnotationUtils.findAnnotation(method, Integrate.ExceptionProcessor.class));
        Integrate.ExceptionProcessor commonExceptionProcessorAnno = empty(AnnotationUtils.findAnnotation(configClass, Integrate.ExceptionProcessor.class));
        EipExceptionParamProcessor exceptionProcessor = new EipExceptionParamProcessor();

        exceptionProcessor.setExceptionPredictFun(optional(exceptionProcessorAnno.exceptionPredictFun(), commonExceptionProcessorAnno.exceptionPredictFun()));
        exceptionProcessor.setExceptionPredictNamespace(optional(exceptionProcessorAnno.exceptionPredictNamespace(), commonExceptionProcessorAnno.exceptionPredictNamespace()));


        String errorCode = optional(exceptionProcessorAnno.errorCode(), commonExceptionProcessorAnno.errorCode());
        String errorMsg = optional(exceptionProcessorAnno.errorMsg(), commonExceptionProcessorAnno.errorMsg());
        CollectionHelper.CollectionBuilder<EipConvertParam, List<EipConvertParam>> collectionBuilder = CollectionHelper.<EipConvertParam>newInstance();
        if (StringUtils.isNotBlank(errorCode)) {
            collectionBuilder.add(new EipConvertParam(errorCode, IEipContext.DEFAULT_ERROR_CODE_KEY).setTargetContextType(ContextTypeEnum.EXECUTOR).construct());
        }
        if (StringUtils.isNotBlank(errorMsg)) {
            collectionBuilder.add(new EipConvertParam(errorCode, IEipContext.DEFAULT_ERROR_MESSAGE_KEY).setTargetContextType(ContextTypeEnum.EXECUTOR).construct());
        }

        List<EipConvertParam> convertParamList = collectionBuilder.build();
        if (CollectionUtils.isNotEmpty(convertParamList)) {
            exceptionProcessor.setConvertParamList(convertParamList);
        }
        return exceptionProcessor;
    }


    private static EipParamProcessor convertResponseParamProcessor(Method method, Class<?> configClass) {
        Integrate.ResponseProcessor responseProcessorAnno = empty(AnnotationUtils.findAnnotation(method, Integrate.ResponseProcessor.class));
        Integrate.ResponseProcessor commonResponseProcessorAnno = empty(AnnotationUtils.findAnnotation(configClass, Integrate.ResponseProcessor.class));
        EipParamProcessor responseProcessor = new EipParamProcessor();
        responseProcessor.setType(ParamProcessorTypeEnum.RESPONSE);

        responseProcessor.setFinalResultKey(optional(responseProcessorAnno.finalResultKey(), commonResponseProcessorAnno.finalResultKey()));

        responseProcessor.setInOutConverterFun(optional(responseProcessorAnno.inOutConverterFun(), commonResponseProcessorAnno.inOutConverterFun()));
        responseProcessor.setInOutConverterNamespace(optional(responseProcessorAnno.inOutConverterNamespace(), commonResponseProcessorAnno.inOutConverterNamespace()));

        responseProcessor.setAuthenticationProcessorFun(optional(responseProcessorAnno.authenticationProcessorFun(), commonResponseProcessorAnno.authenticationProcessorFun()));
        responseProcessor.setAuthenticationProcessorNamespace(optional(responseProcessorAnno.authenticationProcessorNamespace(), commonResponseProcessorAnno.authenticationProcessorNamespace()));

        responseProcessor.setSerializableFun(optional(responseProcessorAnno.serializableFun(), commonResponseProcessorAnno.serializableFun()));
        responseProcessor.setSerializableNamespace(optional(responseProcessorAnno.serializableNamespace(), commonResponseProcessorAnno.serializableNamespace()));

        responseProcessor.setDeserializationFun(optional(responseProcessorAnno.deserializationFun(), commonResponseProcessorAnno.deserializationFun()));
        responseProcessor.setDeserializationNamespace(optional(responseProcessorAnno.deserializationNamespace(), commonResponseProcessorAnno.deserializationNamespace()));

        responseProcessor.setParamConverterCallbackFun(optional(responseProcessorAnno.paramConverterCallbackFun(), commonResponseProcessorAnno.paramConverterCallbackFun()));
        responseProcessor.setParamConverterCallbackNamespace(optional(responseProcessorAnno.paramConverterCallbackNamespace(), commonResponseProcessorAnno.paramConverterCallbackNamespace()));


        Map<String, String> convertParamMap = new HashMap<>();
        bulidConvertParamMap(commonResponseProcessorAnno.convertParams(), convertParamMap);
        bulidConvertParamMap(responseProcessorAnno.convertParams(), convertParamMap);
        CollectionHelper.CollectionBuilder<EipConvertParam, List<EipConvertParam>> collectionBuilder = CollectionHelper.<EipConvertParam>newInstance();
        for (Map.Entry<String, String> entry : convertParamMap.entrySet()) {
            collectionBuilder.add(new EipConvertParam(entry.getKey(), entry.getValue()).construct());
        }
        List<EipConvertParam> convertParamList = collectionBuilder.build();
        if (CollectionUtils.isNotEmpty(convertParamList)) {
            responseProcessor.setConvertParamList(convertParamList);
        }
        return responseProcessor;
    }


    private static EipParamProcessor convertRequestProcessor(Method method, Class<?> configClass, String httpMethod) {

        Integrate.RequestProcessor requestProcessorAnno = empty(AnnotationUtils.findAnnotation(method, Integrate.RequestProcessor.class));
        Integrate.RequestProcessor commonRequestProcessorAnno = empty(AnnotationUtils.findAnnotation(configClass, Integrate.RequestProcessor.class));
        EipParamProcessor requestProcessor = new EipParamProcessor();
        requestProcessor.setType(ParamProcessorTypeEnum.REQUEST);
        requestProcessor.setFinalResultKey(optional(requestProcessorAnno.finalResultKey(), commonRequestProcessorAnno.finalResultKey()));


        requestProcessor.setInOutConverterFun(optional(requestProcessorAnno.inOutConverterFun(), commonRequestProcessorAnno.inOutConverterFun()));
        requestProcessor.setInOutConverterNamespace(optional(requestProcessorAnno.inOutConverterNamespace(), commonRequestProcessorAnno.inOutConverterNamespace()));

        requestProcessor.setAuthenticationProcessorFun(optional(requestProcessorAnno.authenticationProcessorFun(), commonRequestProcessorAnno.authenticationProcessorFun()));
        requestProcessor.setAuthenticationProcessorNamespace(optional(requestProcessorAnno.authenticationProcessorNamespace(), commonRequestProcessorAnno.authenticationProcessorNamespace()));

        requestProcessor.setSerializableFun(optional(requestProcessorAnno.serializableFun(), commonRequestProcessorAnno.serializableFun()));
        requestProcessor.setSerializableNamespace(optional(requestProcessorAnno.serializableNamespace(), commonRequestProcessorAnno.serializableNamespace()));

        requestProcessor.setDeserializationFun(optional(requestProcessorAnno.deserializationFun(), commonRequestProcessorAnno.deserializationFun()));
        requestProcessor.setDeserializationNamespace(optional(requestProcessorAnno.deserializationNamespace(), commonRequestProcessorAnno.deserializationNamespace()));

        requestProcessor.setParamConverterCallbackFun(optional(requestProcessorAnno.paramConverterCallbackFun(), commonRequestProcessorAnno.paramConverterCallbackFun()));
        requestProcessor.setParamConverterCallbackNamespace(optional(requestProcessorAnno.paramConverterCallbackNamespace(), commonRequestProcessorAnno.paramConverterCallbackNamespace()));


        Map<String, String> convertParamMap = new HashMap<>();
        bulidConvertParamMap(commonRequestProcessorAnno.convertParams(), convertParamMap);
        bulidConvertParamMap(requestProcessorAnno.convertParams(), convertParamMap);
        CollectionHelper.CollectionBuilder<EipConvertParam, List<EipConvertParam>> collectionBuilder = CollectionHelper.<EipConvertParam>newInstance();
        for (Map.Entry<String, String> entry : convertParamMap.entrySet()) {
            collectionBuilder.add(new EipConvertParam(entry.getKey(), entry.getValue()).construct());
        }
        List<EipConvertParam> convertParamList = collectionBuilder.build();
        String outParam = IEipContext.HEADER_PARAMS_KEY + "." + Exchange.HTTP_METHOD;
        Optional<EipConvertParam> result = convertParamList.stream().filter(convertParam -> outParam.equals(convertParam.getOutParam())).findAny();
        if (!result.isPresent()) {
            EipConvertParam httpMethodConvertParam = new EipConvertParam("", outParam).setDefaultValue(optional(httpMethod, HttpMethod.POST.name())).construct();
            convertParamList.add(httpMethodConvertParam);
        }
        requestProcessor.setConvertParamList(convertParamList);

        return requestProcessor;
    }

    private static void bulidConvertParamMap(Integrate.ConvertParam[] convertParams, Map<String, String> convertParamMap) {

        if (convertParams != null && convertParams.length > 0) {
            for (Integrate.ConvertParam convertParam : convertParams) {
                convertParamMap.put(convertParam.inParam(), convertParam.outParam());
            }
        }
    }


    private static String optional(String one, String two) {
        String first = StringUtils.isBlank(one) ? null : one;
        String second = StringUtils.isBlank(two) ? null : two;
        return Optional.ofNullable(first).orElse(Optional.ofNullable(second).orElse(null));
    }

    private static Integrate.Advanced empty(Integrate.Advanced advanced) {
        if (advanced == null) {
            return new Integrate.Advanced() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String host() {
                    return null;
                }

                @Override
                public String path() {
                    return null;
                }

                @Override
                public String schema() {
                    return null;
                }

                @Override
                public String httpMethod() {
                    return null;
                }

                @Override
                public int dynamicProtocolCacheSize() {
                    return -1;
                }
            };
        }
        return advanced;
    }

    private static Integrate.ExceptionProcessor empty(Integrate.ExceptionProcessor exceptionProcessor) {
        if (exceptionProcessor == null) {
            return new Integrate.ExceptionProcessor() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String exceptionPredictFun() {
                    return null;
                }

                @Override
                public String exceptionPredictNamespace() {
                    return null;
                }

                @Override
                public String errorMsg() {
                    return null;
                }

                @Override
                public String errorCode() {
                    return null;
                }
            };
        }
        return exceptionProcessor;
    }

    private static Integrate.ResponseProcessor empty(Integrate.ResponseProcessor responseProcessor) {
        if (responseProcessor == null) {
            return new Integrate.ResponseProcessor() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String finalResultKey() {
                    return null;
                }

                @Override
                public String inOutConverterFun() {
                    return null;
                }

                @Override
                public String inOutConverterNamespace() {
                    return null;
                }

                @Override
                public String authenticationProcessorFun() {
                    return null;
                }

                @Override
                public String authenticationProcessorNamespace() {
                    return null;
                }

                @Override
                public String serializableFun() {
                    return null;
                }

                @Override
                public String serializableNamespace() {
                    return null;
                }

                @Override
                public String deserializationFun() {
                    return null;
                }

                @Override
                public String deserializationNamespace() {
                    return null;
                }

                @Override
                public String paramConverterCallbackFun() {
                    return null;
                }

                @Override
                public String paramConverterCallbackNamespace() {
                    return null;
                }

                @Override
                public Integrate.ConvertParam[] convertParams() {
                    return new Integrate.ConvertParam[0];
                }
            };
        }
        return responseProcessor;
    }

    private static Integrate.RequestProcessor empty(Integrate.RequestProcessor requestProcessor) {
        if (requestProcessor == null) {
            return new Integrate.RequestProcessor() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String finalResultKey() {
                    return null;
                }

                @Override
                public String inOutConverterFun() {
                    return null;
                }

                @Override
                public String inOutConverterNamespace() {
                    return null;
                }

                @Override
                public String authenticationProcessorFun() {
                    return null;
                }

                @Override
                public String authenticationProcessorNamespace() {
                    return null;
                }

                @Override
                public String serializableFun() {
                    return null;
                }

                @Override
                public String serializableNamespace() {
                    return null;
                }

                @Override
                public String deserializationFun() {
                    return null;
                }

                @Override
                public String deserializationNamespace() {
                    return null;
                }

                @Override
                public String paramConverterCallbackFun() {
                    return null;
                }

                @Override
                public String paramConverterCallbackNamespace() {
                    return null;
                }

                @Override
                public Integrate.ConvertParam[] convertParams() {
                    return new Integrate.ConvertParam[0];
                }
            };
        }
        return requestProcessor;
    }
}
