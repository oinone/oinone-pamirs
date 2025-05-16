package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

/**
 * 组件技术类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.WidgetTechType", displayName = "组件技术类型")
public class WidgetTechTypeEnum extends BaseEnum<WidgetTechTypeEnum, String> {

    private static final long serialVersionUID = -2396018909042262012L;

    public final static WidgetTechTypeEnum ASYNC = create("ASYNC", "AsyncWidget", "异步组件", "异步组件");
    public final static WidgetTechTypeEnum STATIC = create("STATIC", "StaticWidget", "静态组件", "静态组件");
    public final static WidgetTechTypeEnum VUE = create("VUE", "VueWidget", "常规组件", "常规组件");

}