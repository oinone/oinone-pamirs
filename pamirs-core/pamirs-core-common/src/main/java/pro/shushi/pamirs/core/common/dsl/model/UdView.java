package pro.shushi.pamirs.core.common.dsl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.core.common.dsl.model.view.UdConfig;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.ArrayList;

/**
 * 视图
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_VIEW)
@Model(displayName = "视图")
public class UdView extends UdLayout {

    private static final long serialVersionUID = -9191945006176314723L;
    /**
     * 模型编码
     */
    @XStreamAsAttribute
    @Field(displayName = "模型编码")
    private String model;

    /**
     * 视图类型
     */
    @XStreamAsAttribute
    @Field(displayName = "视图类型")
    private ViewTypeEnum type;

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
     * 基础布局名称
     */
    @XStreamAsAttribute
    @Field(displayName = "基础布局名称")
    private String baseLayoutName;

    /**
     * 开启序号
     * 仅对表格视图有效
     */
    @XStreamAsAttribute
    @Field(displayName = "开启序号")
    private Boolean enableSequence;

    @XStreamAsAttribute
    @Field(displayName = "viewName")
    private String viewName;

    @XStreamAsAttribute
    @Field(displayName = "path")
    private String path;

//    @XStreamAsAttribute
//    @Field(displayName = "filter")
//    private String filter;

    @XStreamAsAttribute
    @Field(displayName = "menu")
    private String menu;

    //如果存在多个searchView, 则search必须为true，且把对应search.xml放在对应菜单目录下
    @XStreamAsAttribute
    @Field(displayName = "search")
    private String search;

    @XStreamAsAttribute
    @Field(displayName = "pagePath")
    private String pagePath;

    @XStreamAsAttribute
    @Field(displayName = "autoFillActions")
    private Boolean autoFillActions;

    public UdView addWidget(UdConfig uiWidget) {
        if (null != uiWidget) {
            if (null == getWidgets()) {
                setWidgets(new ArrayList<>());
            }
            getWidgets().add(uiWidget);
        }
        return this;
    }

}
