package pro.shushi.pamirs.meta.annotation.fun;

import java.lang.annotation.*;

/**
 * D
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface D {

    boolean chain() default true;

}
