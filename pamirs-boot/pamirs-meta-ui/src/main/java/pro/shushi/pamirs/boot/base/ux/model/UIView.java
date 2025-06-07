package pro.shushi.pamirs.boot.base.ux.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.metadata.UIMetadata;
import pro.shushi.pamirs.boot.base.ux.model.view.UIElement;
import pro.shushi.pamirs.framework.orm.xml.converter.CommaSingleValueConverter;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 视图
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_VIEW)
@Model(displayName = "视图")
public class UIView extends UILayout {

    private static final long serialVersionUID = 2635437150778718014L;

    @XStreamAsAttribute
    @Field(displayName = "元数据")
    private UIMetadata metadata;

    /**
     * 模型编码
     */
    @XStreamAsAttribute
    @Field(displayName = "模型编码")
    private String model;

    /**
     * 模型api名称
     */
    @XStreamAsAttribute
    @Field(displayName = "模型api名称")
    private String modelName;

    /**
     * 模型类型
     */
    @XStreamAsAttribute
    @Field(displayName = "模型类型")
    private ModelTypeEnum modelType;

    /**
     * 服务模块名称
     */
    @XStreamAsAttribute
    @Field(displayName = "服务模块名称")
    private String moduleName;

    /**
     * 标题
     */
    @XStreamAsAttribute
    @Field(displayName = "标题")
    private String title;

    /**
     * 描述
     */
    @XStreamAsAttribute
    @Field(displayName = "描述")
    private String summary;

    /**
     * 主键
     */
    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "主键")
    private List<String> pk;

    /**
     * 唯一索引
     */
    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "唯一索引")
    private List<String> uniques;

    /**
     * 索引
     */
    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "索引")
    private List<String> indexes;

    /**
     * 排序
     */
    @XStreamAsAttribute
    @Field(displayName = "排序")
    private String ordering;

    /**
     * 基础布局名称
     */
    @XStreamAsAttribute
    @Field(displayName = "基础布局名称")
    private String layout;

    /**
     * 开启序号
     * 仅对表格视图有效
     */
    @XStreamAsAttribute
    @Field(displayName = "开启序号")
    private Boolean enableSequence;

    /**
     * 默认分组标题
     */
    @XStreamOmitField
    @JSONField(serialize = false)
    private String defaultGroup;

    /**
     * 将所有表格子视图合并为选项卡置于视图底部
     */
    @XStreamOmitField
    @JSONField(serialize = false)
    private Boolean tabsTable;

    public UIView addWidget(UIElement uiWidget) {
        if (null != uiWidget) {
            if (null == getWidgets()) {
                setWidgets(new ArrayList<>());
            }
            getWidgets().add(uiWidget);
        }
        return this;
    }

}
