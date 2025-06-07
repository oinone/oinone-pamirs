package pro.shushi.pamirs.boot.base.ux.annotation.button;

import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;

import java.lang.annotation.*;

/**
 * 跳转按钮
 * <p>
 * 2020/11/16 8:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(UxRouteButton.UxRouteButtons.class)
public @interface UxRouteButton {

    /**
     * 动作基本配置
     *
     * @return 基本配置
     */
    UxAction action();

    /**
     * 窗口动作配置
     *
     * @return 窗口动作配置
     */
    UxRoute value();

    /**
     * 按钮配置列表
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface UxRouteButtons {

        /**
         * 按钮配置
         *
         * @return 按钮配置
         */
        UxRouteButton[] value();

    }

}
