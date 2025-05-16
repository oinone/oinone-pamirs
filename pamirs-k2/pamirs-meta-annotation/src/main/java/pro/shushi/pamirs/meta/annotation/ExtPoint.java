package pro.shushi.pamirs.meta.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtPoint {

    // 展示名称
    String displayName() default "";

    // 描述
    String summary() default "";

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface name {

        // 技术名称，默认为方法名
        java.lang.String value();

    }

    /**
     * 供扩展点接口实现类使用
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Implement {

        // 展示名称
        String displayName() default "";

        // 描述
        String summary() default "";

        // 表达式
        String expression() default "true";

        // 优先级
        int priority() default 99;

    }

}
