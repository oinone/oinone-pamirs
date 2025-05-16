package pro.shushi.pamirs.boot.base.ux.model.view;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * NODE视图
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_NODES)
@Model(displayName = "NODE视图")
public class UINodes extends UIWidget {

    private static final long serialVersionUID = -2695185672919220355L;

}
