package pro.shushi.pamirs.meta.annotation;

import java.lang.annotation.*;

/**
 * 映射
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Documented
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {

    // 键
    String name();

    // 值
    String value();

    // 值类型，只支持原始类型
    Class<?> type() default String.class;

}
