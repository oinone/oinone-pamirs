package pro.shushi.pamirs.core.common.dsl.model;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.boot.base.ux.enmu.layout.AlignTypeEnum;
import pro.shushi.pamirs.boot.base.ux.enmu.layout.LayoutTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 布局
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model(displayName = "布局")
public abstract class UdLayout extends UdWidget {

    private static final long serialVersionUID = -5137575534528021970L;

    /**
     * 布局方式
     */
    @XStreamAsAttribute
    @Field(displayName = "布局方式")
    private LayoutTypeEnum layout;

    /**
     * 是否换行
     */
    @XStreamAsAttribute
    @Field(displayName = "是否换行")
    private Boolean wrap;

    /**
     * 水平栅格数
     */
    @XStreamAsAttribute
    @Field(displayName = "水平栅格数")
    private Integer cols;

    /**
     * 垂直栅格数
     */
    @XStreamAsAttribute
    @Field(displayName = "垂直栅格数")
    private Integer rows;

    /**
     * 水平及垂直方向间隔
     */
    @XStreamAsAttribute
    @Field(displayName = "水平及垂直方向间隔")
    private Integer gap;

    /**
     * 水平方向间隔
     */
    @XStreamAsAttribute
    @Field(displayName = "水平方向间隔")
    private Integer colGap;

    /**
     * 垂直方向间隔
     */
    @XStreamAsAttribute
    @Field(displayName = "垂直方向间隔")
    private Integer rowGap;

    /**
     * 水平方向对齐方式
     */
    @XStreamAsAttribute
    @Field(displayName = "水平方向对齐方式")
    private AlignTypeEnum colAlign;

    /**
     * 垂直方向对齐方式
     */
    @XStreamAsAttribute
    @Field(displayName = "垂直方向对齐方式")
    private AlignTypeEnum rowAlign;

}
