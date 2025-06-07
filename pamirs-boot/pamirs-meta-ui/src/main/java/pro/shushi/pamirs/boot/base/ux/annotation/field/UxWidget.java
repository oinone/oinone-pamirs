package pro.shushi.pamirs.boot.base.ux.annotation.field;

import org.springframework.core.annotation.AliasFor;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.meta.annotation.Prop;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;

import java.lang.annotation.*;

/**
 * 自定义组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Documented
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UxWidget {

    @AliasFor("label")
    String value() default "";

    // 显示名称
    @AliasFor("value")
    String label() default "";

    // 组件
    String widget() default "";

    // 组件配置参数
    Prop[] config() default {};

    // 数据传输映射DSL
    Prop[] mapping() default {};

    // 上下文
    Prop[] context() default {};

    // 查询方式
    QueryModeEnum queryMode() default QueryModeEnum.DOMAIN;

    // -- 布局（默认流式布局）

    // 块所占栅格
    int span() default GridConstants.defaultBlockViewGrid;

    // 栅格左侧的间隔格数，间隔内不可以有栅格
    int offset() default 0;

    // -- 提示信息

    // 占位提示
    String placeholder() default "";

    // 说明提示
    String hint() default "";

    // -- 显示与隐藏

    // 必填
    String required() default "";

    // 只读
    String readonly() default "";

    // 隐藏
    String invisible() default "";

    // 禁用
    String disable() default "";

    // -- 分组与顺序

    // 分组
    // 开启新的分组并设置分组标题，默认为融入前序组件的分组
    String group() default CharacterConstants.SEPARATOR_HYPHEN;

    // 选项卡页
    // 在分组中开启新的选项卡页并设置选项卡页标题，默认为融入前序组件的选项卡页
    String tab() default CharacterConstants.SEPARATOR_HYPHEN;

    // 不再融入前序组件的选项卡
    boolean breakTab() default false;

    // 优先级
    int priority() default MetaDefaultConstants.FAKE_PRIORITY_VALUE_INT;

}
