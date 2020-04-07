package pro.shushi.pamirs.meta.annotation;

import java.lang.annotation.*;

/**
 * 标识枚举的类注解
 * @author deng
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    // 数据字典编码
    String dictionary();

    // 显示名称
    String displayName() default "";

    // 字段技术名称
    String name() default "";

}
