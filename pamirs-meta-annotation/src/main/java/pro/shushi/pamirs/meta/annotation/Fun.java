package pro.shushi.pamirs.meta.annotation;

import java.lang.annotation.*;

/**
 * 标识支持函数的类注解
 * @author deng(d@shushi.pro)
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Fun {

    // 命名空间，不可更改，默认与全限定类名相同
    // action配置在非模型类中时，需要配置此属性指定模型编码，扩展点需要配置此属性指定所扩展函数命名空间
    // 如果自定义value值，则字段无法直接使用该函数
    String value() default "pamirs";

}
