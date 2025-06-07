package pro.shushi.pamirs.meta.annotation.validation;

import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionScopeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验注解
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {

    // 模型约束-校验函数（校验函数编码列表）
    String[] check() default {};

    // 模型约束-校验函数（函数编码及提示列表）
    Fun[] checkWithTips() default {};

    // 模型约束-校验表达式（校验表达式列表）
    String[] rule() default {};

    // 模型约束-校验表达式（表达式及提示列表）
    Rule[] ruleWithTips() default {};

    /**
     * 校验表达式
     */
    @Target({ElementType.TYPE_PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Rule {

        // 内容
        java.lang.String value();

        // 表达式提示
        java.lang.String tips() default "";

        // 错误提示
        java.lang.String error();

        // 错误类型
        ErrorTypeEnum errorType() default ErrorTypeEnum.BIZ_ERROR;

        // 信息级别
        InformationLevelEnum level() default InformationLevelEnum.ERROR;

        // 备注
        java.lang.String remark() default "";

        // 执行域
        FunctionScopeEnum scope() default FunctionScopeEnum.BOTH;

    }

    /**
     * 校验函数
     */
    @Target({ElementType.TYPE_PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Fun {

        // 内容
        String value();

        // 校验提示
        String tips();

        // 备注
        java.lang.String remark() default "";

        // 执行域
        FunctionScopeEnum scope() default FunctionScopeEnum.BOTH;

    }

}