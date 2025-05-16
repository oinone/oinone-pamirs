package pro.shushi.pamirs.core.common.dsl.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.base.enmu.WidgetBizTypeEnum;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
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
@XStreamAlias(DslConstants.NODE_WIDGET)
@Model(displayName = "组件")
public class UdWidget extends UdLayoutCell {

    private static final long serialVersionUID = -3683824287852557774L;

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
    private String startGroup;

    /**
     * 选项卡页
     * 在分组中开启新的选项卡页并设置选项卡页标题，默认为融入前序组件的选项卡页
     */
    @XStreamOmitField
    @JSONField(serialize = false)
    private String tab;

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
    @XStreamOmitField
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
     * 子组件列表
     */
    @XStreamImplicit
    @Field(displayName = "子组件列表")
    private List<UdWidget> widgets;

    public UdWidget setPropList(List<Prop> propList) {
        if (CollectionUtils.isNotEmpty(propList)) {
            for (Prop prop : propList) {
                get_d().put(prop.getName(), prop.getValue());
            }
        }
        return this;
    }

    public UdWidget addProp(Prop prop) {
        if (null != prop) {
            get_d().put(prop.getName(), prop.getValue());
        }
        return this;
    }

    public UdWidget addWidget(UdWidget uiWidget) {
        if (null != uiWidget) {
            if (null == getWidgets()) {
                setWidgets(new ArrayList<>());
            }
            getWidgets().add(uiWidget);
        }
        return this;
    }

}
