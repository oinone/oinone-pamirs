package pro.shushi.pamirs.boot.base.ux.annotation.button;

import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxClient;

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
@Repeatable(UxClientButton.UxClientButtons.class)
public @interface UxClientButton {

    /**
     * 动作基本配置
     *
     * @return 基本配置
     */
    UxAction action();

    /**
     * 客户端动作配置
     *
     * @return 客户端动作配置
     */
    UxClient value();

    /**
     * 按钮配置列表
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface UxClientButtons {

        /**
         * 按钮配置
         *
         * @return 按钮配置
         */
        UxClientButton[] value();

    }

}
