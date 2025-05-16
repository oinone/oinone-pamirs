package pro.shushi.pamirs.boot.base.ux.annotation.field;

import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.lang.annotation.*;

/**
 * 指定视图忽略该组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UxIgnore {

    ViewTypeEnum[] value() default {};

}
