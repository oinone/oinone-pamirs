package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.base.Empty;

import java.lang.annotation.*;

/**
 * 标识支持扩展点的类注解
 *
 * @author deng(d # shushi.pro)
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ext {

    // 扩展点所扩展函数所在类
    Class<?> value() default Empty.class;

}
