package pro.shushi.pamirs.boot.base.ux.annotation.action;

import pro.shushi.pamirs.meta.annotation.Prop;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动作基本配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UxAction {

    /**
     * 动作名称
     *
     * @return 动作名称
     */
    String name();

    // 展示名称
    String displayName() default "";

    // 显示文字
    String label() default "";

    // 描述
    String summary() default "";

    // 上下文类型
    ActionContextTypeEnum contextType() default ActionContextTypeEnum.SINGLE;

    // action绑定在源模型上的哪些视图上
    ViewTypeEnum[] bindingType() default {ViewTypeEnum.TABLE};

    // 客户端显隐表达式
    String invisible() default "";

    // 服务端过滤表达式
    String rule() default "";

    // 禁用规则
    String disable() default "";

    // 绑定视图名称
    // 设置动作只出现在指定视图
    String bindingView() default "";

    // 优先级
    int priority() default 99;

    // 扩展属性
    Prop[] props() default {};

}
