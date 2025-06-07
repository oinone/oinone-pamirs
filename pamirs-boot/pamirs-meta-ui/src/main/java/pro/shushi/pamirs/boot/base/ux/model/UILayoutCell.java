package pro.shushi.pamirs.boot.base.ux.model;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 布局单元
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@Model(displayName = "布局单元")
public abstract class UILayoutCell extends TransientModel {

    private static final long serialVersionUID = -3215171643534945054L;

    /**
     * 宽
     */
    @XStreamAsAttribute
    @Field(displayName = "宽")
    private String width;

    /**
     * 高
     */
    @XStreamAsAttribute
    @Field(displayName = "高")
    private String height;

    /**
     * 水平方向开始于第几个栅格，可简写为start
     */
    @XStreamAsAttribute
    @Field(displayName = "水平方向开始于第几个栅格")
    private Integer start;
    @XStreamAsAttribute
    @Field(displayName = "水平方向开始于第几个栅格")
    private Integer colStart;

    /**
     * 水平方向占几个栅格，可简写为span
     */
    @XStreamAsAttribute
    @Field(displayName = "水平方向占几个栅格")
    private Integer span;
    @XStreamAsAttribute
    @Field(displayName = "水平方向占几个栅格")
    private String colSpan;

    /**
     * 垂直方向开始于第几个栅格
     */
    @XStreamAsAttribute
    @Field(displayName = "垂直方向开始于第几个栅格")
    private Integer rowStart;

    /**
     * 垂直方向占几个栅格
     */
    @XStreamAsAttribute
    @Field(displayName = "垂直方向占几个栅格")
    private Integer rowSpan;

    /**
     * 栅格左侧间隔格数
     * 间隔内不可以有栅格
     */
    @XStreamAsAttribute
    @Field(displayName = "栅格左侧间隔格数")
    private Integer offset;

}
