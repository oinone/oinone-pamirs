package pro.shushi.pamirs.meta.annotation.sys;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 元模型注解
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaModel {

    // 排序
    @AliasFor("priority")
    int value() default 50;

    @AliasFor("value")
    int priority() default 50;

    // 进入核心计算的扫描类型：module#Class、model#Class、field#Field、function#Method
    Class<?>[] core() default {};

}
