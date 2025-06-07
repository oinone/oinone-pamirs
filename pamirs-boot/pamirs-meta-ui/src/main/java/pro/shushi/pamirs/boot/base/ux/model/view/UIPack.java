package pro.shushi.pamirs.boot.base.ux.model.view;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 容器组件
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_PACK)
@Model(displayName = "容器组件")
public class UIPack extends UIWidget {

    private static final long serialVersionUID = 4780277575425337223L;

    /**
     * 标题
     */
    @XStreamAsAttribute
    @Field(displayName = "标题")
    private String title;

    /**
     * 边框
     */
    @XStreamAsAttribute
    @Field(displayName = "边框")
    private Boolean border;

}
