package pro.shushi.pamirs.core.common.dsl.model.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 清除配置
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_CLEAR)
@Model(displayName = "清除配置")
public class UdClear extends UdWidget {

    private static final long serialVersionUID = 3756715407443774718L;

}
