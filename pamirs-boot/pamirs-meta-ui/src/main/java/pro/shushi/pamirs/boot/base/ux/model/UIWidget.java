package pro.shushi.pamirs.boot.base.ux.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.thoughtworks.xstream.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.base.enmu.WidgetBizTypeEnum;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.framework.orm.xml.converter.CommaSingleValueConverter;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.model.Prop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 组件
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_WIDGET)
@Model(displayName = "组件")
public class UIWidget extends UILayoutCell {

    private static final long serialVersionUID = -7299059629790712151L;

    /**
     * api名称
     */
    @XStreamAsAttribute
    @Field(displayName = "api名称")
    private String name;

    /**
     * 业务类型
     */
    @XStreamAsAttribute
    @Field(displayName = "业务类型")
    private WidgetBizTypeEnum bizType;

    /**
     * 组件名称
     */
    @XStreamAsAttribute
    @Field(displayName = "组件名称")
    private String widget;

    /**
     * DSL节点类型
     */
    @XStreamAsAttribute
    @Field(displayName = "DSL类型")
    private String dslNodeType;

    /**
     * 上下文
     */
    @XStreamAsAttribute
    @Field(displayName = "上下文")
    private Map<String, Object> context;

    /**
     * 隐藏
     */
    @XStreamAsAttribute
    @Field(displayName = "隐藏")
    private String invisible;

    /**
     * 分组
     * 开启新的分组并设置分组标题，默认为融入前序组件的分组
     */
    @XStreamOmitField
    @JSONField(serialize = false)
    private String newGroup;

    /**
     * 选项卡页
     * 在分组中开启新的选项卡页并设置选项卡页标题，默认为融入前序组件的选项卡页
     */
    @XStreamOmitField
    @JSONField(serialize = false)
    private String newTab;

    /**
     * 结束选项卡
     * 不再融入前序组件的选项卡
     */
    @XStreamOmitField
    @JSONField(serialize = false)
    private Boolean breakTab;

    /**
     * 优先级
     */
    @Field(displayName = "优先级")
    @XStreamAsAttribute
    @JSONField(serialize = false)
    private Integer priority;

    /**
     * 模板插槽
     */
    @XStreamAsAttribute
    @Field(displayName = "模板插槽")
    private String slot;

    /**
     * 模板插槽支持类型
     */
    @XStreamAsAttribute
    @Field(displayName = "模板插槽支持类型")
    private String slotSupport;

    /**
     * 客户端指令集
     */
    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "客户端指令集")
    private List<String> directives;

    /**
     * 自动填充
     */
    @XStreamAsAttribute
    @Field(displayName = "自动填充")
    private Boolean autoFill;

    /**
     * 子组件列表
     */
    @XStreamImplicit
    @Field(displayName = "子组件列表")
    private List<UIWidget> widgets;

    @JSONField(serialize = false)
    @XStreamOmitField
    private boolean compiled;

    public Object getPropValue(String name) {
        return get_d().get(name);
    }

    public UIWidget setPropList(List<Prop> propList) {
        if (CollectionUtils.isNotEmpty(propList)) {
            for (Prop prop : propList) {
                get_d().put(prop.getName(), prop.getValue());
            }
        }
        return this;
    }

    public UIWidget addProp(Prop prop) {
        if (null != prop) {
            get_d().put(prop.getName(), prop.getValue());
        }
        return this;
    }

    public UIWidget addWidget(UIWidget uiWidget) {
        if (null != uiWidget) {
            if (null == getWidgets()) {
                setWidgets(new ArrayList<>());
            }
            getWidgets().add(uiWidget);
        }
        return this;
    }

}
