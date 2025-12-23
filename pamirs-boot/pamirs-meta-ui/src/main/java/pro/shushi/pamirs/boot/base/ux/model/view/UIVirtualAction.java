package pro.shushi.pamirs.boot.base.ux.model.view;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * @author yeshenyue on 2025/12/18 17:22.
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_VIRTUAL_ACTION)
@Model(displayName = "虚拟动作")
public class UIVirtualAction extends UIAction {

    private static final long serialVersionUID = 8581609323501995103L;

    @XStreamAsAttribute
    @Field(displayName = "动作字段", summary = "实际执行的动作，动作从该字段数据中获取")
    private String actionField;

    @XStreamAsAttribute
    @Field(displayName = "参数字段名称")
    private String argument;

    @XStreamAsAttribute
    @Field(displayName = "是否虚拟动作")
    private Boolean isVirtual;
}
