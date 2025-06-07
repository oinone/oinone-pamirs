package pro.shushi.pamirs.core.common.dsl.model.mask;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 页头
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(TemplateNodeConstants.NODE_HEADER)
@Model(displayName = "页头")
public class UdHeader extends UdContainer {

    private static final long serialVersionUID = 1535683846609738305L;

}
