package pro.shushi.pamirs.meta.annotation.x;

import java.lang.annotation.*;

/**
 * 服务注解
 *
 * @author deng(d @ shushi.pro)
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XService {

    // 是否发布远程服务
    boolean publish() default true;

}
