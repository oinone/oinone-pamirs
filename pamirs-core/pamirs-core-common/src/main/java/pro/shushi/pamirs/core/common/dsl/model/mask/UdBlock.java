package pro.shushi.pamirs.core.common.dsl.model.mask;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 块组件
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_BLOCK)
@Model(displayName = "块组件")
public class UdBlock extends UdContainer {

    private static final long serialVersionUID = 3974930607749690658L;

}
