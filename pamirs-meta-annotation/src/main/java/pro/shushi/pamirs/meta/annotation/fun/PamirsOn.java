package pro.shushi.pamirs.meta.annotation.fun;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author deng
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PamirsOn {

    String name() default "";

    boolean fun() default false;// 是否生成fun变量

    String version() default "";

}
