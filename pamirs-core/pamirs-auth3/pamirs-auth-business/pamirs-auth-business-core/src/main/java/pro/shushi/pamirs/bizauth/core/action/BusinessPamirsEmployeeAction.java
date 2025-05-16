package pro.shushi.pamirs.bizauth.core.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.bizauth.api.tmodel.PamirsEmployeeRoleTransient;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

/**
 * {@link PamirsEmployee}动作
 *
 */
@Component
@Model.model(PamirsEmployee.MODEL_MODEL)
@UxRouteButton(action = @UxAction(name = "batchBindingRole",
        label = "绑定角色", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH),
        value = @UxRoute(model = PamirsEmployeeRoleTransient.MODEL_MODEL, viewType = ViewTypeEnum.FORM,
                openType = ActionTargetEnum.DIALOG, viewName = "modify_employee_role"))
public class BusinessPamirsEmployeeAction {

}
