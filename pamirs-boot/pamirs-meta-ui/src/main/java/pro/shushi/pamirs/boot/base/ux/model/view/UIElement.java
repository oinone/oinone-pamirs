package pro.shushi.pamirs.boot.base.ux.model.view;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 功能组件
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_ELEMENT)
@Model(displayName = "功能组件")
public class UIElement extends UIWidget {

    private static final long serialVersionUID = -1200809631897765953L;

}
