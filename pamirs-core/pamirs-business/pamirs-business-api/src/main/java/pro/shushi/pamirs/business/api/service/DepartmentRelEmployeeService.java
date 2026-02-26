package pro.shushi.pamirs.business.api.service;

import jakarta.annotation.Nullable;
import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * {@link DepartmentRelEmployee}服务
 *
 * @author Adamancy Zhang at 14:54 on 2021-09-26
 */
@Fun(DepartmentRelEmployeeService.FUN_NAMESPACE)
public interface DepartmentRelEmployeeService extends StandardModelService<DepartmentRelEmployee> {

    String FUN_NAMESPACE = "business.DepartmentRelEmployeeService";

    @Function
    @Nullable
    @Override
    DepartmentRelEmployee create(DepartmentRelEmployee data);

    @Function
    @Nullable
    @Override
    DepartmentRelEmployee update(DepartmentRelEmployee data);

    @Function
    @Override
    Integer updateByWrapper(DepartmentRelEmployee data, LambdaUpdateWrapper<DepartmentRelEmployee> wrapper);

    @Function
    @Override
    List<DepartmentRelEmployee> delete(List<DepartmentRelEmployee> list);

    @Function
    @Nullable
    @Override
    DepartmentRelEmployee deleteOne(DepartmentRelEmployee data);

    @Function
    @Override
    Pagination<DepartmentRelEmployee> queryPage(Pagination<DepartmentRelEmployee> page, LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper);

    @Function
    @Nullable
    @Override
    DepartmentRelEmployee queryOne(DepartmentRelEmployee query);

    @Function
    @Nullable
    @Override
    DepartmentRelEmployee queryOneByWrapper(LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper);

    @Function
    @Override
    List<DepartmentRelEmployee> queryListByWrapper(LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper);

    @Function
    PamirsEmployee queryDepartmentSupervisor(PamirsDepartment department);

    @Function
    PamirsEmployee fillDepartmentDataByEmployee(PamirsEmployee employee);

    @Function
    List<String> queryDeptCodeByEmpCode(String employeeCode);

    @Function
    Integer assignDepartmentSupervisor(String departmentCode, String employeeCode);

    @Function
    void clearImmediateSupervisorByDept(PamirsDepartment data, List<PamirsEmployee> employeeList, List<PamirsEmployee> originEmployees);

    @Function
    Integer assignSupervisorOrImmediateSupervisor(PamirsEmployee employee, List<PamirsDepartment> departmentList, List<PamirsDepartment> originDepartmentList);

    @Function
    void clearImmediateSupervisorCodes(Set<String> employeeCodes);
}
