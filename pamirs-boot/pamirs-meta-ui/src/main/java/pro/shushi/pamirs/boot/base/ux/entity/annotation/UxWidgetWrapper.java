package pro.shushi.pamirs.boot.base.ux.entity.annotation;

import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.meta.annotation.Prop;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * {@link UxWidget} annotation wrapper class.
 *
 * @author Adamancy Zhang at 15:17 on 2025-06-16
 */
@Data
public class UxWidgetWrapper implements UxWidget, Serializable {

    private static final long serialVersionUID = -3345282445204827707L;

    private String label;

    private String widget;

    private List<UxPropWrapper> config;

    private List<UxPropWrapper> mapping;

    private List<UxPropWrapper> context;

    private QueryModeEnum queryMode = QueryModeEnum.DOMAIN;

    private int span = GridConstants.defaultBlockViewGrid;

    private int offset = 0;

    private String placeholder;

    private String hint;

    private String required;

    private String readonly;

    private String invisible;

    private String disable;

    private String group;

    private String tab;

    private boolean breakTab = false;

    private int priority = MetaDefaultConstants.FAKE_PRIORITY_VALUE_INT;

    @Override
    public String value() {
        return label;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public String widget() {
        return widget;
    }

    @Override
    public Prop[] config() {
        if (config == null) {
            return new Prop[0];
        }
        return config.toArray(new Prop[0]);
    }

    @Override
    public Prop[] mapping() {
        if (mapping == null) {
            return new Prop[0];
        }
        return mapping.toArray(new Prop[0]);
    }

    @Override
    public Prop[] context() {
        if (context == null) {
            return new Prop[0];
        }
        return context.toArray(new Prop[0]);
    }

    @Override
    public QueryModeEnum queryMode() {
        return queryMode;
    }

    @Override
    public int span() {
        return span;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public String placeholder() {
        return placeholder;
    }

    @Override
    public String hint() {
        return hint;
    }

    @Override
    public String required() {
        return required;
    }

    @Override
    public String readonly() {
        return readonly;
    }

    @Override
    public String invisible() {
        return invisible;
    }

    @Override
    public String disable() {
        return disable;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String tab() {
        return tab;
    }

    @Override
    public boolean breakTab() {
        return breakTab;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return UxWidget.class;
    }

    private void addConfig(Prop prop) {
        if (config == null) {
            config = new ArrayList<>();
        }
        config.add(UxPropWrapper.wrap(prop));
    }

    private void addMapping(Prop prop) {
        if (mapping == null) {
            mapping = new ArrayList<>();
        }
        mapping.add(UxPropWrapper.wrap(prop));
    }

    private void addContext(Prop prop) {
        if (context == null) {
            context = new ArrayList<>();
        }
        context.add(UxPropWrapper.wrap(prop));
    }

    public static UxWidgetWrapper wrap(UxWidget widget) {
        UxWidgetWrapper uxWidget = new UxWidgetWrapper();
        uxWidget.label = widget.label();
        uxWidget.widget = widget.widget();
        Optional.ofNullable(widget.config()).map(Arrays::stream).ifPresent(v -> v.forEach(uxWidget::addConfig));
        Optional.ofNullable(widget.mapping()).map(Arrays::stream).ifPresent(v -> v.forEach(uxWidget::addMapping));
        Optional.ofNullable(widget.context()).map(Arrays::stream).ifPresent(v -> v.forEach(uxWidget::addContext));
        uxWidget.queryMode = widget.queryMode();
        uxWidget.span = widget.span();
        uxWidget.offset = widget.offset();
        uxWidget.placeholder = widget.placeholder();
        uxWidget.hint = widget.hint();
        uxWidget.required = widget.required();
        uxWidget.readonly = widget.readonly();
        uxWidget.invisible = widget.invisible();
        uxWidget.disable = widget.disable();
        uxWidget.group = widget.group();
        uxWidget.tab = widget.tab();
        uxWidget.breakTab = widget.breakTab();
        uxWidget.priority = widget.priority();
        return uxWidget;
    }
}
