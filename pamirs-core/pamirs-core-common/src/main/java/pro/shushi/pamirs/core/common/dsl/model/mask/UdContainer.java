package pro.shushi.pamirs.core.common.dsl.model.mask;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 容器组件
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(TemplateNodeConstants.NODE_CONTAINER)
@Model(displayName = "容器")
public class UdContainer extends UdWidget {

    private static final long serialVersionUID = 2949069667212956682L;

}
