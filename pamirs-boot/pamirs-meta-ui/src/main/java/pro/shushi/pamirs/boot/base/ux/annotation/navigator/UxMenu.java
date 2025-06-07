package pro.shushi.pamirs.boot.base.ux.annotation.navigator;

import org.springframework.core.annotation.AliasFor;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;

import java.lang.annotation.*;

/**
 * 菜单
 * <p>
 * 2020/11/16 8:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UxMenu {

    String name() default "";

    @AliasFor("label")
    String value() default "";

    @AliasFor("value")
    String label() default "";

    String summary() default "";

    String icon() default "";

    ClientTypeEnum[] clientTypes() default {ClientTypeEnum.PC, ClientTypeEnum.MOBILE, ClientTypeEnum.PAD};
}
