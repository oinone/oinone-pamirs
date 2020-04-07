package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动作
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

    // 描述
    String summary() default "";

    // 上下文类型
    ActionContextTypeEnum contextType() default ActionContextTypeEnum.SINGLE;

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
        FunctionTypeEnum methodType() default FunctionTypeEnum.JAVA;

        // 动作用途
        FunctionUsageEnum usage() default FunctionUsageEnum.WRITE;

    }

}
