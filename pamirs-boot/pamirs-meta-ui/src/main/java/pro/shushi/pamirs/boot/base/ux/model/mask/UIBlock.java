package pro.shushi.pamirs.boot.base.ux.model.mask;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 块级元素
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_BLOCK)
@Model(displayName = "块级元素")
public class UIBlock extends UIWidget {

    private static final long serialVersionUID = 2041706798773834320L;

}
