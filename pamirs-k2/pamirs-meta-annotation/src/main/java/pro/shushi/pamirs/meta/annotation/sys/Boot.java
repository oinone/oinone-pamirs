package pro.shushi.pamirs.meta.annotation.sys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否自动安装的引导启动项
 *
 * @author deng d@shushi.pro
 * date 2019.04.10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Boot {

    // 是否引导项
    boolean value() default true;

}
