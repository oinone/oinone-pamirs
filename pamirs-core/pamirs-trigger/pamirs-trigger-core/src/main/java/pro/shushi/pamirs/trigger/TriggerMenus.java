package pro.shushi.pamirs.trigger;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;
import pro.shushi.pamirs.trigger.tmodel.TaskActionTransientModel;

/**
 * Business管理后台菜单
 * <p>
 * 2020/11/18 5:08 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings({"unused", "InnerClassMayBeStatic"})// @formatter:off 忽略格式化，请开启IDE格式化注解开关

@UxMenus /*可以注解到该模块的任意类上，建议同一个模块中只配置一处*/
class TriggerMenus implements ViewActionConstants {
    @UxMenu("执行任务")
    @UxRoute(model = TaskActionTransientModel.MODEL_MODEL, viewName = "任务table", module = TriggerModule.MODULE_MODULE)
    class PamirsOrganizationMenu {
    }

    @UxMenu("计划任务")
    @UxRoute(model = TaskActionTransientModel.MODEL_MODEL, viewName = "任务table")
    class PamirsDepartmentMenu {
    }

    @UxMenu("触发任务")
    @UxRoute(model = TriggerTaskAction.MODEL_MODEL, viewName = "触发任务table")
    class PamirsPositionMenu {
    }
}
