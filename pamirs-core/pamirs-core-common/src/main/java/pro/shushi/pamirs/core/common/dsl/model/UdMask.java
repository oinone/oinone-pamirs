package pro.shushi.pamirs.core.common.dsl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 母版
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_MASK)
@Model(displayName = "母版")
public class UdMask extends UdLayout {

    private static final long serialVersionUID = -3273572349287468586L;

}
