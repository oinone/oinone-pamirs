package pro.shushi.pamirs.boot.base.ux.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxIgnore;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxDetail;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTable;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTableSearch;
import pro.shushi.pamirs.boot.base.ux.entity.annotation.UxWidgetWrapper;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * Ux模型字段实体类
 *
 * @author Adamancy Zhang at 15:22 on 2025-06-16
 */
@Data
public class UxModelFieldEntity implements Serializable {

    private static final long serialVersionUID = -383209350488280577L;

    private String field;

    private UxWidgetWrapper tableFieldWidget;

    private UxWidgetWrapper searchFieldWidget;

    private UxWidgetWrapper registerSearchWidget;

    private UxWidgetWrapper formFieldWidget;

    private UxWidgetWrapper detailFieldWidget;

    private List<ViewTypeEnum> ignoredViewTypes;

    @JSONField(serialize = false)
    private transient ModelFieldConfig modelFieldConfig;

    public UxWidgetWrapper getFieldWidgetByViewType(ViewTypeEnum viewType) {
        switch (viewType) {
            case TABLE:
                return tableFieldWidget;
            case SEARCH:
                return searchFieldWidget;
            case FORM:
                return formFieldWidget;
            case DETAIL:
                return detailFieldWidget;
            default:
                throw new UnsupportedOperationException("Invalid view type. viewType = " + viewType);
        }
    }

    public static UxModelFieldEntity wrap(Field field) {
        UxModelFieldEntity uxModelField = new UxModelFieldEntity();
        pro.shushi.pamirs.meta.annotation.Field fieldAnnotation = AnnotationUtils.findAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.class);
        if (fieldAnnotation == null) {
            return null;
        }
        uxModelField.field = Optional.ofNullable(AnnotationUtils.findAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.field.class))
                .map(pro.shushi.pamirs.meta.annotation.Field.field::value)
                .orElse(field.getName());
        Optional.ofNullable(AnnotationUtils.findAnnotation(field, UxTable.FieldWidget.class))
                .map(UxTable.FieldWidget::value)
                .map(UxWidgetWrapper::wrap)
                .ifPresent(uxModelField::setTableFieldWidget);
        Optional.ofNullable(AnnotationUtils.findAnnotation(field, UxTableSearch.FieldWidget.class))
                .map(UxTableSearch.FieldWidget::value)
                .map(UxWidgetWrapper::wrap)
                .ifPresent(uxModelField::setSearchFieldWidget);
        Optional.ofNullable(AnnotationUtils.findAnnotation(field, UxForm.FieldWidget.class))
                .map(UxForm.FieldWidget::value)
                .map(UxWidgetWrapper::wrap)
                .ifPresent(uxModelField::setFormFieldWidget);
        Optional.ofNullable(AnnotationUtils.findAnnotation(field, UxDetail.FieldWidget.class))
                .map(UxDetail.FieldWidget::value)
                .map(UxWidgetWrapper::wrap)
                .ifPresent(uxModelField::setDetailFieldWidget);
        Optional.ofNullable(AnnotationUtils.findAnnotation(field, UxIgnore.class))
                .map(UxIgnore::value)
                .map(Lists::newArrayList)
                .ifPresent(uxModelField::setIgnoredViewTypes);
        return uxModelField;
    }
}
