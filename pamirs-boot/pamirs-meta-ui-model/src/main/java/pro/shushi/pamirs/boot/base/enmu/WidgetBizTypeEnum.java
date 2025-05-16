package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

/**
 * 组件业务类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.WidgetBizType", displayName = "组件业务类型")
public class WidgetBizTypeEnum extends BaseEnum<WidgetBizTypeEnum, String> {

    private static final long serialVersionUID = 8712255367167291977L;

    public final static WidgetBizTypeEnum VIEW = create("VIEW", "ViewWidget", "视图组件", "视图组件");
    public final static WidgetBizTypeEnum PACK = create("PACK", "Pack", "容器组件", "容器组件");
    public final static WidgetBizTypeEnum ELEMENT = create("ELEMENT", "Element", "元素组件", "元素组件");
    public final static WidgetBizTypeEnum FIELD = create("FIELD", "FieldWidget", "字段组件", "字段组件");
    public final static WidgetBizTypeEnum ACTION = create("ACTION", "ActionWidget", "动作组件", "动作组件");
    public final static WidgetBizTypeEnum SLOT = create("SLOT", "Slot", "插槽", "插槽");

}