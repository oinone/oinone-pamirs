package pro.shushi.pamirs.core.common.dsl.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 搜索视图
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_SEARCH)
@Model(displayName = "搜索视图")
public class UdSearch extends UdView {

    private static final long serialVersionUID = 8346874312378027948L;

}
