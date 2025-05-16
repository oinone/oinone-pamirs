package pro.shushi.pamirs.boot.web.spi.domain;

import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.constants.GridConstants;
import pro.shushi.pamirs.meta.annotation.Prop;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;

import java.lang.annotation.Annotation;

/**
 * 注册视图 构造搜索字段Widget
 */
public class RegisterSearchWidget implements UxWidget {

    String value = "";

    // 显示名称
    String label = "";

    // 组件
    String widget = "";

    // 组件配置参数
    Prop[] config;

    // 数据传输映射DSL
    Prop[] mapping;

    // 查询方式
    QueryModeEnum queryMode = QueryModeEnum.DOMAIN;

    // -- 布局（默认流式布局）
    // 块所占栅格
    int span = GridConstants.defaultBlockViewGrid;

    // 栅格左侧的间隔格数，间隔内不可以有栅格
    int offset = 0;

    // -- 提示信息
    // 占位提示
    String placeholder = "";

    // 优先级
    int priority = MetaDefaultConstants.FAKE_PRIORITY_VALUE_INT;

    @Override
    public String value() {
        return value;
    }

    public RegisterSearchWidget setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String label() {
        return label;
    }

    public RegisterSearchWidget setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String widget() {
        return widget;
    }

    public RegisterSearchWidget setWidget(String widget) {
        this.widget = widget;
        return this;
    }

    @Override
    public Prop[] config() {
        return config;
    }

    public RegisterSearchWidget setConfig(Prop[] config) {
        this.config = config;
        return this;
    }

    @Override
    public Prop[] mapping() {
        return mapping;
    }

    @Override
    public Prop[] context() {
        return new Prop[0];
    }

    public RegisterSearchWidget setMapping(Prop[] mapping) {
        this.mapping = mapping;
        return this;
    }

    @Override
    public QueryModeEnum queryMode() {
        return queryMode;
    }

    public RegisterSearchWidget setQueryMode(QueryModeEnum queryMode) {
        this.queryMode = queryMode;
        return this;
    }

    @Override
    public int span() {
        return span;
    }

    public RegisterSearchWidget setSpan(int span) {
        this.span = span;
        return this;
    }

    @Override
    public int offset() {
        return offset;
    }

    public RegisterSearchWidget setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public String placeholder() {
        return placeholder;
    }

    public RegisterSearchWidget setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public int priority() {
        return priority;
    }

    public RegisterSearchWidget setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public String hint() {
        return null;
    }

    @Override
    public String required() {
        return null;
    }

    @Override
    public String readonly() {
        return null;
    }

    @Override
    public String invisible() {
        return null;
    }

    @Override
    public String disable() {
        return null;
    }

    @Override
    public String group() {
        return null;
    }

    @Override
    public String tab() {
        return null;
    }

    @Override
    public boolean breakTab() {
        return false;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    public static RegisterSearchWidget ofDefault() {
        return new RegisterSearchWidget();
    }
}
