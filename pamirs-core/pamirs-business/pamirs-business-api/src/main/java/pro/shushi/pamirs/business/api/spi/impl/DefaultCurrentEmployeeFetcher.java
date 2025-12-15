package pro.shushi.pamirs.business.api.spi.impl;

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
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 获取当前员工默认实现
 *
 * @author Adamancy Zhang at 17:41 on 2025-12-01
 */
@Order
@Component
@SPI.Service
public class DefaultCurrentEmployeeFetcher implements CurrentEmployeeFetcher {

    @Override
    public PamirsEmployee fetch() {
        String employeeCode = EmployeeSession.getEmployeeCode();
        if (StringUtils.isNotBlank(employeeCode)) {
            return Models.origin().queryOneByWrapper(generatorWrapper().eq(PamirsEmployee::getCode, employeeCode));
        }
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            return null;
        }
        List<PamirsEmployee> employeeList = Models.origin().queryListByWrapper(
                new Pagination<>(1, 1),
                generatorWrapper().eq(PamirsEmployee::getBindingUserId, userId)
        );
        if (CollectionUtils.isEmpty(employeeList)) {
            return null;
        }
        return employeeList.get(0);
    }

    @Override
    public List<PamirsEmployee> fetchList() {
        Set<String> employeeCodes = EmployeeSession.getEmployeeCodes();
        if (employeeCodes == null) {
            Long userId = PamirsSession.getUserId();
            if (userId == null) {
                return null;
            }
            List<PamirsEmployee> employeeList = Models.origin().queryListByWrapper(generatorWrapper().eq(PamirsEmployee::getBindingUserId, userId));
            if (CollectionUtils.isEmpty(employeeList)) {
                return null;
            }
            return employeeList;
        }
        if (employeeCodes.isEmpty()) {
            return null;
        }
        return Models.origin().queryListByWrapper(generatorWrapper().in(PamirsEmployee::getCode, employeeCodes));
    }

    @Override
    public List<PamirsEmployee> fetchDeptEmployeeList() {
        Set<String> departmentCodes = getDepartmentCodes();
        if (CollectionUtils.isEmpty(departmentCodes)) {
            return null;
        }
        Set<String> employeeCodes = fetchEmployeeCodesByDepartmentCodes(departmentCodes);
        if (CollectionUtils.isEmpty(employeeCodes)) {
            return null;
        }
        return DataShardingHelper.build().collectionSharding(employeeCodes, (sublist) -> Models.origin().queryListByWrapper(
                generatorWrapper().setBatchSize(-1).in(PamirsEmployee::getCode, sublist))
        );
    }

    protected Set<String> getDepartmentCodes() {
        Set<String> departmentCodes = DepartmentSession.getDepartmentCodes();
        if (departmentCodes == null) {
            List<PamirsDepartment> departments = CurrentDepartmentFetcher.get().fetchList();
            if (CollectionUtils.isEmpty(departments)) {
                return null;
            }
            departmentCodes = departments.stream().map(PamirsDepartment::getCode).collect(Collectors.toSet());
        }
        return departmentCodes;
    }

    @Override
    public List<PamirsEmployee> fetchDeptWithChildrenEmployeeList() {
        Set<String> departmentCodes = getDepartmentCodesWithChildren();
        if (CollectionUtils.isEmpty(departmentCodes)) {
            return null;
        }
        Set<String> employeeCodes = fetchEmployeeCodesByDepartmentCodes(departmentCodes);
        if (CollectionUtils.isEmpty(employeeCodes)) {
            return null;
        }
        return DataShardingHelper.build().collectionSharding(employeeCodes, (sublist) -> Models.origin().queryListByWrapper(
                generatorWrapper().setBatchSize(-1).in(PamirsEmployee::getCode, sublist))
        );
    }

    protected Set<String> getDepartmentCodesWithChildren() {
        Set<String> departmentCodes = DepartmentSession.getDepartmentCodesWithChildren();
        if (departmentCodes == null) {
            List<PamirsDepartment> departments = CurrentDepartmentFetcher.get().fetchListWithChildren();
            if (CollectionUtils.isEmpty(departments)) {
                return null;
            }
            departmentCodes = departments.stream().map(PamirsDepartment::getCode).collect(Collectors.toSet());
        }
        return departmentCodes;
    }

    protected Set<String> fetchEmployeeCodesByDepartmentCodes(Set<String> departmentCodes) {
        List<DepartmentRelEmployee> departmentRelEmployees = DataShardingHelper.build().collectionSharding(departmentCodes, (sublist) -> Models.origin().queryListByWrapper(
                Pops.<DepartmentRelEmployee>lambdaQuery()
                        .from(DepartmentRelEmployee.MODEL_MODEL)
                        .in(DepartmentRelEmployee::getDepartmentCode, sublist)));
        if (CollectionUtils.isEmpty(departmentRelEmployees)) {
            return null;
        }
        return departmentRelEmployees.stream().map(DepartmentRelEmployee::getEmployeeCode).collect(Collectors.toSet());
    }

    protected LambdaQueryWrapper<PamirsEmployee> generatorWrapper() {
        return Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL)
                .select(PamirsEmployee::getId, PamirsEmployee::getCode, PamirsEmployee::getEmployeeType,
                        PamirsEmployee::getCompanyCode,
                        PamirsEmployee::getDepartmentCode, PamirsEmployee::getDepartmentTreeCode
                )
                .eq(PamirsEmployee::getDataStatus, DataStatusEnum.ENABLED.value());
    }
}
