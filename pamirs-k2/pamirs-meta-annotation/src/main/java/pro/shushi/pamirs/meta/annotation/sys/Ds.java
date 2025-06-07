package pro.shushi.pamirs.meta.annotation.sys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源路由
 *
 * @author deng d@shushi.pro
 * date 2019.04.10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Ds {

    // 直接指定路由数据源
    String value() default "";

    // 根据模型编码获取路由数据源，优先级低于直接指定路由数据源
    // 若既未直接指定路由数据源，又未配置模型编码，则取方法第一个入参作为模型编码
    String model() default "";

}