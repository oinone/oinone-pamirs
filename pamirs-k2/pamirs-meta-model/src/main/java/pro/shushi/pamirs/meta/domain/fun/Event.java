package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 事件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.Event")
@Model(displayName = "事件")
public class Event extends IdModel {

    private static final long serialVersionUID = 2302466599970741188L;

    @Base
    @Field(displayName = "显示名称", required = true)
    private String displayName;

    @Base
    @Field(displayName = "源函数")
    private FunctionDefinition sourceFunction;

    @Field(displayName = "描述", required = true)
    private String description;

    @Base
    @Field(displayName = "是否激活")
    private Boolean active;

}
