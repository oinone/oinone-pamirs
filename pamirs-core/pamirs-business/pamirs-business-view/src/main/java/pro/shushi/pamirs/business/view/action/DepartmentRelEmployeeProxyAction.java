package pro.shushi.pamirs.business.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.business.api.pmodel.DepartmentRelEmployeeProxy;
import pro.shushi.pamirs.business.api.service.DepartmentRelEmployeeProxyService;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * @author yeshenyue on 2025/5/27 14:37.
 */
@Slf4j
@Component
@Model.model(DepartmentRelEmployeeProxy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "LoadAddEmployeeListByDept", displayName = "部门添加员工", label = "部门添加员工", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = DepartmentRelEmployeeProxy.MODEL_MODEL, viewName = "部门添加员工", openType = ActionTargetEnum.DIALOG, load = "queryCanEmployeeChoose"))
@UxRouteButton(
        action = @UxAction(name = "LoadAddDeptListByEmployee", displayName = "员工添加部门", label = "员工添加部门", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = DepartmentRelEmployeeProxy.MODEL_MODEL, viewName = "员工添加部门", openType = ActionTargetEnum.DIALOG, load = "queryCanDepartmentChoose"))
public class DepartmentRelEmployeeProxyAction {

    @Autowired
    private DepartmentRelEmployeeProxyService departmentRelEmployeeProxyService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<DepartmentRelEmployeeProxy> queryCanEmployeeChoose(Pagination<DepartmentRelEmployeeProxy> page, IWrapper<DepartmentRelEmployeeProxy> queryWrapper) {
        return departmentRelEmployeeProxyService.queryCanEmployeeChoose(page, queryWrapper);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<DepartmentRelEmployeeProxy> queryCanDepartmentChoose(Pagination<DepartmentRelEmployeeProxy> page, IWrapper<DepartmentRelEmployeeProxy> queryWrapper) {
        return departmentRelEmployeeProxyService.queryCanDepartmentChoose(page, queryWrapper);
    }
}
