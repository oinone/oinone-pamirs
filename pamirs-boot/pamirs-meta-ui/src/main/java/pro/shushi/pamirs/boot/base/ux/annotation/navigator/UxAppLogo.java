package pro.shushi.pamirs.boot.base.ux.annotation.navigator;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 应用logo配置
 * <p>
 * 2020/11/16 7:26 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UxAppLogo {

    // 应用logo
    @AliasFor("logo")
    String value() default "";

    @AliasFor("value")
    String logo() default "";

}
