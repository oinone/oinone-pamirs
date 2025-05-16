package pro.shushi.pamirs.boot.base.ux.annotation.view;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;

import java.lang.annotation.*;

/**
 * 详情视图
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UxDetail {

    // 栅格
    int grid() default GridConstants.defaultViewGrid;

    // 默认分组标题
    String group() default "";

    // 将所有表格子视图合并为选项卡置于视图底部
    boolean tabsTable() default true;

    // 字段组件
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface FieldWidget {

        UxWidget value();

    }

}
