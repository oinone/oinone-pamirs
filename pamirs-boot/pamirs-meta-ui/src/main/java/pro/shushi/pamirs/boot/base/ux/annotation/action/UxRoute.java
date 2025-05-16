package pro.shushi.pamirs.boot.base.ux.annotation.action;

import org.springframework.core.annotation.AliasFor;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.meta.annotation.Prop;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.lang.annotation.*;

/**
 * 窗口动作
 * <p>
 * 2020/11/16 8:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UxRoute {

    // 目标模型编码
    @AliasFor("model")
    String value() default "";

    // 目标模型编码
    @AliasFor("value")
    String model() default "";

    // 指定目标视图，可缺省，使用默认视图
    String viewName() default "";

    // 视图类型
    ViewTypeEnum viewType() default ViewTypeEnum.TABLE;

    // 打开方式
    ActionTargetEnum openType() default ActionTargetEnum.ROUTER;

    // 目标模块编码
    String module() default "";

    // 页面标题
    String title() default "";

    // 主题
    String theme() default "";

    // 母版
    String mask() default "";

    // 支持可供切换的视图类型列表
    ViewTypeEnum[] views() default {};

    // 数据加载方式
    QueryModeEnum queryMode() default QueryModeEnum.DOMAIN;

    // 数据加载函数编码
    String load() default "";

    // 数据传输映射DSL
    Prop[] mapping() default {};

    // 上下文
    Prop[] context() default {};

    // 数据过滤-客户端
    String domain() default "";

    // 数据过滤-服务端
    String filter() default "";

    // 初始化页面数据数量限制
    int limit() default 20;

}
