package pro.shushi.pamirs.boot.base.ux.annotation.action;

import pro.shushi.pamirs.meta.annotation.Prop;

import java.lang.annotation.*;

/**
 * 服务器动作
 * <p>
 * 2020/11/16 8:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UxServer {

    // 模型编码
    String model() default "";

    // 动作名称
    String name() default "";

    // 数据传输映射DSL
    Prop[] mapping() default {};

    // 上下文
    Prop[] context() default {};

}
