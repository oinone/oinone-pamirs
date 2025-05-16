package pro.shushi.pamirs.core.common.dsl.model.view;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 配置
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_CONFIG)
@Model(displayName = "功能组件")
public class UdConfig extends UdWidget {

    private static final long serialVersionUID = 5153406862143506275L;

}
