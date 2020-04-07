package pro.shushi.pamirs.meta.annotation.sys;

import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否是系统创建不可更改注解
 *
 * @author deng d@shushi.pro
 * date 2019.04.10
 *
 */
@Target({ElementType.TYPE,ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base {

    // 系统来源
    SystemSourceEnum value() default SystemSourceEnum.BASE;

}
