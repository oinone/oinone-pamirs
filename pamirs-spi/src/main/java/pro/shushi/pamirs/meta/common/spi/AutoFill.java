package pro.shushi.pamirs.meta.common.spi;

import java.lang.annotation.*;

/**
 * 自动填充注解
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 2:30 上午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AutoFill {

}