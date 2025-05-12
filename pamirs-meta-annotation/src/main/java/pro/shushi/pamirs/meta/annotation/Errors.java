package pro.shushi.pamirs.meta.annotation;

import java.lang.annotation.*;

/**
 * 错误定义注解
 *
 * @author deng
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Errors {

    // 显示名称
    String displayName();

    // 描述
    String summary() default "";

}
