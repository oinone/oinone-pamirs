package pro.shushi.pamirs.boot.base.ux.annotation.navigator;

import java.lang.annotation.*;

/**
 * 菜单集合
 * <p>
 * 2020/11/16 8:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UxMenus {

    /**
     * 菜单所属模块，默认为当前模块
     *
     * @return 指定菜单模块
     */
    String module() default "";

    /**
     * 菜单起始优先级，默认为0
     *
     * @return 菜单起始优先级
     */
    int basePriority() default 0;
}
