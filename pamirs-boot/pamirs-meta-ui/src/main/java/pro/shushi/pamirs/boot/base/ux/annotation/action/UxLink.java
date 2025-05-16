package pro.shushi.pamirs.boot.base.ux.annotation.action;

import org.springframework.core.annotation.AliasFor;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.meta.annotation.Prop;

import java.lang.annotation.*;

/**
 * 链接动作
 * <p>
 * 2020/11/16 8:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UxLink {

    // 链接表达式
    @AliasFor("url")
    String value() default "";

    // 链接表达式
    @AliasFor("value")
    String url() default "";

    // 打开方式
    ActionTargetEnum openType() default ActionTargetEnum.ROUTER;

    // 链接计算函数模型
    String model() default "";

    // 链接计算函数编码
    String compute() default "";

    // 数据传输映射DSL
    Prop[] mapping() default {};

    // 上下文配置
    Prop[] context() default {};

}
