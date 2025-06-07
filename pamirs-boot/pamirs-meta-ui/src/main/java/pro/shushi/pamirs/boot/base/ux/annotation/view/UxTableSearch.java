package pro.shushi.pamirs.boot.base.ux.annotation.view;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;

import java.lang.annotation.*;

/**
 * 列表搜索配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UxTableSearch {

    // 栅格
    int grid() default GridConstants.defaultTableSearchGrid;

    // 字段组件
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface FieldWidget {

        UxWidget value();

    }

}
