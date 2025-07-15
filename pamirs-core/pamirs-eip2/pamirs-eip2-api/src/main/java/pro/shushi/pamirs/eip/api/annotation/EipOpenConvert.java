package pro.shushi.pamirs.eip.api.annotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.enmu.EipOpenConverterTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public class EipOpenConvert {
    public static EipOpenInterface convert(Function function, Method method, IEipAnnotationSingletonConfig eipAnnotationSingletonConfig) {
        Open open = AnnotationUtils.findAnnotation(method, Open.class);
        if (open == null) {
            return null;
        }

        Open.Advanced openAdvanced = empty(AnnotationUtils.findAnnotation(method, Open.Advanced.class));
        Open.Advanced commonIntegrateAdvanced;
        Class<?> configClass = open.config();
        if (configClass.equals(Void.class)) {
            commonIntegrateAdvanced = empty(null);
        } else {
            commonIntegrateAdvanced = empty(AnnotationUtils.findAnnotation(configClass, Open.Advanced.class));
        }
        IEipAnnotationSingletonConfig eipConfigModel = null;
        if (!configClass.equals(Void.class) && eipAnnotationSingletonConfig != null) {
            try {
                eipConfigModel = (IEipAnnotationSingletonConfig) ((IEipAnnotationSingletonConfig) configClass.newInstance()).singletonModel();
                if (eipConfigModel == null) {
                    //没有初始化
                    return null;
                }
                if (!eipAnnotationSingletonConfig.getKey().equals(eipConfigModel.getKey())) {
                    return null;
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String httpMethod = optional(openAdvanced.httpMethod(), "post");
        String path = StringUtils.isNotBlank(open.path()) ? open.path() : function.getFun();
        path = URLHelper.repairAbsolutePathPrefix(path);
        String finalResultKey = EipContextConstant.RESULT_KEY;

        EipOpenInterface eipOpenInterface = new EipOpenInterface();
        eipOpenInterface.setConverterType(EipOpenConverterTypeEnum.EIP_FUNCTION_WITH_RESULT);
        eipOpenInterface.setConverterNamespace(function.getNamespace());
        eipOpenInterface.setConverterFun(function.getFun());
        eipOpenInterface.setUri("rest:" + httpMethod + ":" + EipContextConstant.OPEN_API_ROUTE_PREFIX + "pamirs" + path);
        eipOpenInterface.setFinalResultKey(finalResultKey);
        eipOpenInterface.setModule(function.getModule());
        eipOpenInterface.setName(StringUtils.isNotBlank(open.name()) ? open.name() : function.getDisplayName());
        eipOpenInterface.setInterfaceName(function.getNamespace() + "#" + function.getFun());


        eipOpenInterface.setInOutConverterFun(optional(openAdvanced.inOutConverterFun(), commonIntegrateAdvanced.inOutConverterFun()));
        eipOpenInterface.setInOutConverterNamespace(optional(openAdvanced.inOutConverterNamespace(), commonIntegrateAdvanced.inOutConverterNamespace()));

        eipOpenInterface.setAuthenticationProcessorFun(optional(openAdvanced.authenticationProcessorFun(), commonIntegrateAdvanced.authenticationProcessorFun()));
        eipOpenInterface.setAuthenticationProcessorNamespace(optional(openAdvanced.authenticationProcessorNamespace(), commonIntegrateAdvanced.authenticationProcessorNamespace()));

        eipOpenInterface.setSerializableFun(optional(openAdvanced.serializableFun(), commonIntegrateAdvanced.serializableFun()));
        eipOpenInterface.setSerializableNamespace(optional(openAdvanced.serializableNamespace(), commonIntegrateAdvanced.serializableNamespace()));

        eipOpenInterface.setDeserializationFun(optional(openAdvanced.deserializationFun(), commonIntegrateAdvanced.deserializationFun()));
        eipOpenInterface.setDeserializationNamespace(optional(openAdvanced.deserializationNamespace(), commonIntegrateAdvanced.deserializationNamespace()));


        eipOpenInterface.construct();
        return eipOpenInterface;
    }

    private static String optional(String one, String two) {
        String first = StringUtils.isBlank(one) ? null : one;
        String second = StringUtils.isBlank(two) ? null : two;
        return Optional.ofNullable(first).orElse(Optional.ofNullable(second).orElse(null));
    }

    private static Open.Advanced empty(Open.Advanced advanced) {
        if (advanced == null) {
            return new Open.Advanced() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String httpMethod() {
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

            };
        }
        return advanced;
    }
}
