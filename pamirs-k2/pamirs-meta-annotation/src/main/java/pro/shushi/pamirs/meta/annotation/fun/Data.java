package pro.shushi.pamirs.meta.annotation.fun;

import java.lang.annotation.*;

/**
 * get set生成注解
 *
 * @author deng
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {

    boolean chain() default true;

    String staticConstructor() default "";

}
