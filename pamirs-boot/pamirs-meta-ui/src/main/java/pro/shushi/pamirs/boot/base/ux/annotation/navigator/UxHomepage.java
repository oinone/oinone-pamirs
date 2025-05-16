package pro.shushi.pamirs.boot.base.ux.annotation.navigator;

import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;

import java.lang.annotation.*;

/**
 * 模块首页配置
 * <p>
 * 2020/11/16 7:26 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UxHomepage {

    // 引用的action名称. 不指定则创建
    String actionName() default "";

    // 路由
    UxRoute value();

}
