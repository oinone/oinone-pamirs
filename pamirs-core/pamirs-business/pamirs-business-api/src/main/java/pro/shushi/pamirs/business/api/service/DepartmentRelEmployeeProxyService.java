package pro.shushi.pamirs.business.api.service;

import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.pmodel.DepartmentRelEmployeeProxy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * @author yeshenyue on 2025/5/27 17:02.
 */
@Fun(DepartmentRelEmployeeProxyService.FUN_NAMESPACE)
public interface DepartmentRelEmployeeProxyService {

    String FUN_NAMESPACE = "business.DepartmentRelEmployeeProxyService";

    /**
     * 分页查询可供部门选择的员工
     */
    @Function
    Pagination<DepartmentRelEmployeeProxy> queryCanEmployeeChoose(Pagination<DepartmentRelEmployeeProxy> page, IWrapper<DepartmentRelEmployeeProxy> queryWrapper);

    /**
     * 分页查询员工可选择的部门
     */
    @Function
    Pagination<DepartmentRelEmployeeProxy> queryCanDepartmentChoose(Pagination<DepartmentRelEmployeeProxy> page, IWrapper<DepartmentRelEmployeeProxy> queryWrapper);

    /**
     * 更新员工部门关系
     */
    @Function
    void updateRelationDepartment(PamirsDepartment department);

    /**
     * 更新员工部门关系
     */
    @Function
    void updateRelationEmployee(PamirsEmployee employee);
}
