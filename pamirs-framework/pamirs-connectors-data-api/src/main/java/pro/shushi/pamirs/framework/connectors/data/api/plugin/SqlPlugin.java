package pro.shushi.pamirs.framework.connectors.data.api.plugin;

import java.lang.annotation.*;

/**
 * sql插件注解
 *
 * @author hubin
 * @since 2018-01-13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SqlPlugin {

    /**
     * 过滤 SQL 解析，默认 false
     */
    boolean filter() default false;
}
