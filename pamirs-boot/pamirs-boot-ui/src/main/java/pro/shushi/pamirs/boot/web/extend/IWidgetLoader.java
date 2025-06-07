package pro.shushi.pamirs.boot.web.extend;

import pro.shushi.pamirs.boot.base.model.WidgetDefinition;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * 加载前段低代码文件
 */
@Fun(IWidgetLoader.FUN_NAMESPACE)
public interface IWidgetLoader {
    String FUN_NAMESPACE = "base.IWidgetLoader";

    /**
     * 参照以下方法定义,自行增加一个方法返回前端低代码文件
     * 目的: 独立部署时,根据namespace获取function列表,通过function的远程调用能力,一次性加载所有的前端低代码文件
     *
     * @return
     */
    default WidgetDefinition example() {
        return null;
    }
}
