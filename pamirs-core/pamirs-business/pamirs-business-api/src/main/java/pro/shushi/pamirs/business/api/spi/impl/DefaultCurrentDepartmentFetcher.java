package pro.shushi.pamirs.business.api.spi.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.session.DepartmentSession;
import pro.shushi.pamirs.business.api.session.EmployeeSession;
import pro.shushi.pamirs.business.api.spi.CurrentDepartmentFetcher;
import pro.shushi.pamirs.business.api.spi.CurrentEmployeeFetcher;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取当前部门默认实现
 *
 * @author Adamancy Zhang at 18:03 on 2025-12-01
 */
@Order
@Component
@SPI.Service
public class DefaultCurrentDepartmentFetcher implements CurrentDepartmentFetcher {

    @Override
    public PamirsDepartment fetch() {
        String departmentCode = getCurrentDepartmentCodeBySession();
        if (StringUtils.isBlank(departmentCode)) {
            departmentCode = getCurrentDepartmentCodeByEmployee();
            if (StringUtils.isBlank(departmentCode)) {
                return null;
            }
        }
        return Models.origin().queryOneByWrapper(generatorWrapper().eq(PamirsDepartment::getCode, departmentCode));
    }

    protected String getCurrentDepartmentCodeBySession() {
        String departmentCode = DepartmentSession.getDepartmentCode();
        if (StringUtils.isNotBlank(departmentCode)) {
            return departmentCode;
        }
        String employeeType = EmployeeSession.getEmployeeType();
        String employeeCode = EmployeeSession.getEmployeeCode();
        if (StringUtils.isAnyBlank(employeeType, employeeCode)) {
            return null;
        }
        List<DepartmentRelEmployee> departmentRelEmployees = Models.origin().queryListByWrapper(
                new Pagination<>(1, 1),
                Pops.<DepartmentRelEmployee>lambdaQuery()
                        .from(DepartmentRelEmployee.MODEL_MODEL)
                        .eq(DepartmentRelEmployee::getEmployeeType, employeeType)
                        .eq(DepartmentRelEmployee::getEmployeeCode, employeeCode));
        if (CollectionUtils.isEmpty(departmentRelEmployees)) {
            return null;
        }
        return departmentRelEmployees.get(0).getDepartmentCode();
    }

    protected String getCurrentDepartmentCodeByEmployee() {
        PamirsEmployee employee = CurrentEmployeeFetcher.get().fetch();
        if (employee == null) {
            return null;
        }
        return employee.getDepartmentCode();
    }

    @Override
    public List<PamirsDepartment> fetchList() {
        PamirsDepartment department = fetch();
        if (department == null) {
            return null;
        }
        return fillDepartmentChildren(Lists.newArrayList(department), new HashSet<>());
    }

    protected List<PamirsDepartment> fillDepartmentChildren(List<PamirsDepartment> departments, Set<String> existCodes) {
        // FIXME: zbh 20251202 此处可通过 TreeCode 优化查询子部门
        List<PamirsDepartment> validDepartments = new ArrayList<>();
        Set<String> parentCodes = new HashSet<>();
        for (PamirsDepartment department : departments) {
            String code = department.getCode();
            if (existCodes.contains(code)) {
                continue;
            }
            existCodes.add(code);
            parentCodes.add(code);
            validDepartments.add(department);
        }
        if (CollectionUtils.isEmpty(parentCodes)) {
            return validDepartments;
        }
        List<PamirsDepartment> nextDepartments = DataShardingHelper.build().collectionSharding(parentCodes, (sublist) -> Models.origin().queryListByWrapper(
                generatorWrapper().setBatchSize(-1).in(PamirsDepartment::getParentCode, sublist))
        );
        if (CollectionUtils.isEmpty(nextDepartments)) {
            return validDepartments;
        }
        validDepartments.addAll(fillDepartmentChildren(nextDepartments, existCodes));
        return validDepartments;
    }

    protected LambdaQueryWrapper<PamirsDepartment> generatorWrapper() {
        return Pops.<PamirsDepartment>lambdaQuery()
                .from(PamirsDepartment.MODEL_MODEL)
                .select(PamirsDepartment::getId, PamirsDepartment::getCode, PamirsDepartment::getTreeCode, PamirsDepartment::getDepartmentType);
    }
}
