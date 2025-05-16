package pro.shushi.pamirs.eip.api.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Open {
    String name() default "";

    //配置类
    Class<?> config() default Void.class;

    String path() default "";

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Advanced {
        String httpMethod() default "";

        String inOutConverterFun() default "";

        String inOutConverterNamespace() default "";

        String authenticationProcessorFun() default "";

        String authenticationProcessorNamespace() default "";

        String serializableFun() default "";

        String serializableNamespace() default "";

        String deserializationFun() default "";

        String deserializationNamespace() default "";
    }
}
