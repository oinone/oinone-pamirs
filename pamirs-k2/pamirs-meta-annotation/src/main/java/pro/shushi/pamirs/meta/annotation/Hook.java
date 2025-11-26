package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.common.constants.MetaValueConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.HookTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hook {

    String displayName() default "";

    /**
     * @deprecated 6.x please using HookBefore/HookAfter interface
     */
    @Deprecated
    HookTypeEnum hookType() default HookTypeEnum.BEFORE;

    FunctionTypeEnum[] functionTypes() default {};

    /**
     * 执行优先级，越小的先执行
     */
    int priority() default MetaValueConstants.priorityForHook;

    String[] module() default {};

    String[] model() default {};

    String[] fun() default {};

    String description() default "";

    boolean active() default true;

}
