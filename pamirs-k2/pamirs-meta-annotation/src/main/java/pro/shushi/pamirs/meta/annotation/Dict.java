package pro.shushi.pamirs.meta.annotation;

import java.lang.annotation.*;

/**
 * 标识枚举的类注解
 *
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

    // 技术名称
    String name() default "";

    // 描述
    String summary() default "";

    // 类型
    int type() default 1;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface dictionary {
        // 数据字典的编码
        String value();
    }

}
