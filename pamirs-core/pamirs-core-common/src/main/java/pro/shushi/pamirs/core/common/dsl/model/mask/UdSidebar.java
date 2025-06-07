package pro.shushi.pamirs.core.common.dsl.model.mask;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 边栏
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(TemplateNodeConstants.NODE_SIDEBAR)
@Model(displayName = "边栏")
public class UdSidebar extends UdContainer {

    private static final long serialVersionUID = 2489987651354920998L;

}
