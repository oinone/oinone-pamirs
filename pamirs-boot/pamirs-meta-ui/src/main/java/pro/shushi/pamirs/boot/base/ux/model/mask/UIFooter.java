package pro.shushi.pamirs.boot.base.ux.model.mask;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 页尾
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_FOOTER)
@Model(displayName = "页尾")
public class UIFooter extends UIContainer {

    private static final long serialVersionUID = 148690313426344949L;

}
