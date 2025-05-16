package pro.shushi.pamirs.core.common.dsl.model.view;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 模板插槽填写
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_PLACEHOLDER)
@Model(displayName = "模板插槽内容")
public class UdPlaceholder extends UdWidget {

    private static final long serialVersionUID = -3406930145599042726L;

}
