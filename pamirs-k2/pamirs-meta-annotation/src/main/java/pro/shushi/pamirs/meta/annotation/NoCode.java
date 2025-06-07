package pro.shushi.pamirs.meta.annotation;

import java.lang.annotation.*;

/**
 * 无代码
 *
 * @author deng
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoCode {

}
