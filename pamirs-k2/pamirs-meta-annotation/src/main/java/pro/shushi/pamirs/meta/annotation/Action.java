package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务器动作
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    // 展示名称
    String displayName() default "";

    // 显示文字
    String label() default "";

    // 描述
    String summary() default "";

    // 上下文类型
    ActionContextTypeEnum contextType() default ActionContextTypeEnum.SINGLE;

    // 数据传输映射DSL
    Prop[] mapping() default {};

    // 上下文
    Prop[] context() default {};

    // 扩展属性
    Prop[] attributes() default {};

    // action绑定在源模型上的哪些视图上
    ViewTypeEnum[] bindingType() default {ViewTypeEnum.TABLE};

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Advanced {

        // 技术名称
        String name() default "";

        // 参数名列表
        String[] args() default {};

        // 动作函数类型
        FunctionTypeEnum[] type() default FunctionTypeEnum.UPDATE;

        // 动作函数语言
        FunctionLanguageEnum language() default FunctionLanguageEnum.JAVA;

        // 是否是数据库管理器函数
        boolean managed() default false;

        // 显隐表达式
        String invisible() default "";

        // 服务端过滤表达式
        String rule() default "";

        // 禁用表达式
        String disable() default "";

        // 校验
        boolean check() default false;

        // 绑定视图名称
        // 设置动作只出现在指定视图
        String bindingView() default "";

        // 优先级
        int priority() default 100;

    }

}
