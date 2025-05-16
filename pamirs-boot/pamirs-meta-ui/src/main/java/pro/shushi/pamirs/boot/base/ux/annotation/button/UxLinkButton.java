package pro.shushi.pamirs.boot.base.ux.annotation.button;

import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxLink;

import java.lang.annotation.*;

/**
 * 链接按钮
 * <p>
 * 2020/11/16 8:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(UxLinkButton.UxLinkButtons.class)
public @interface UxLinkButton {

    /**
     * 动作基本配置
     *
     * @return 基本配置
     */
    UxAction action();

    /**
     * 链接动作配置
     *
     * @return 链接动作配置
     */
    UxLink value();

    /**
     * 按钮配置列表
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface UxLinkButtons {

        /**
         * 按钮配置
         *
         * @return 按钮配置
         */
        UxLinkButton[] value();

    }

}
