package pro.shushi.pamirs.eip.api.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Integrate {
    String name() default "";

    //配置类
    Class<?> config();

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Advanced {
        //请求uri = schema://host/path
        String host() default "";

        String path() default "";

        String schema() default "";

        String httpMethod() default "";

        int dynamicProtocolCacheSize() default -1;
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface RequestProcessor {
        String finalResultKey() default "";

        String inOutConverterFun() default "";

        String inOutConverterNamespace() default "";

        String authenticationProcessorFun() default "";

        String authenticationProcessorNamespace() default "";

        String serializableFun() default "";

        String serializableNamespace() default "";

        String deserializationFun() default "";

        String deserializationNamespace() default "";

        String paramConverterCallbackFun() default "";

        String paramConverterCallbackNamespace() default "";

        ConvertParam[] convertParams() default {};
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ResponseProcessor {
        String finalResultKey() default "";

        String inOutConverterFun() default "";

        String inOutConverterNamespace() default "";

        String authenticationProcessorFun() default "";

        String authenticationProcessorNamespace() default "";

        String serializableFun() default "";

        String serializableNamespace() default "";

        String deserializationFun() default "";

        String deserializationNamespace() default "";

        String paramConverterCallbackFun() default "";

        String paramConverterCallbackNamespace() default "";

        ConvertParam[] convertParams() default {};
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ExceptionProcessor {
        String exceptionPredictFun() default "";

        String exceptionPredictNamespace() default "";

        String errorMsg() default "";

        String errorCode() default "";
    }

    @Target({ElementType.TYPE_PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ConvertParam {
        String inParam();

        String outParam();
    }

}
