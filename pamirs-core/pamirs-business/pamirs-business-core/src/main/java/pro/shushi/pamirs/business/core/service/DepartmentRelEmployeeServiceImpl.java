package pro.shushi.pamirs.business.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.service.DepartmentRelEmployeeService;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link DepartmentRelEmployeeService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Slf4j
@Service
@Fun(DepartmentRelEmployeeService.FUN_NAMESPACE)
public class DepartmentRelEmployeeServiceImpl extends AbstractStandardModelService<DepartmentRelEmployee> implements DepartmentRelEmployeeService {

    @Function
    @Override
    public DepartmentRelEmployee create(DepartmentRelEmployee data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<DepartmentRelEmployee> createBatch(List<DepartmentRelEmployee> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public DepartmentRelEmployee update(DepartmentRelEmployee data) {
        return super.update(data);
    }

    @Function
    @Override
    public DepartmentRelEmployee createOrUpdate(DepartmentRelEmployee data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<DepartmentRelEmployee> delete(List<DepartmentRelEmployee> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public DepartmentRelEmployee deleteOne(DepartmentRelEmployee data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<DepartmentRelEmployee> queryPage(Pagination<DepartmentRelEmployee> page, LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public DepartmentRelEmployee queryOne(DepartmentRelEmployee query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public DepartmentRelEmployee queryOneByWrapper(LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<DepartmentRelEmployee> queryListByWrapper(LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<DepartmentRelEmployee> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Function
    @Override
    public PamirsEmployee queryDepartmentSupervisor(PamirsDepartment department) {
        if (department == null || StringUtils.isBlank(department.getCode())) {
            log.error("Department or department code is empty");
            return null;
        }
        List<DepartmentRelEmployee> rels = queryListByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getDepartmentCode, department.getCode())
                .eq(DepartmentRelEmployee::getDepartmentType, department.getDepartmentType())
                .eq(DepartmentRelEmployee::getSupervisor, Boolean.TRUE)
        );
        if (CollectionUtils.isEmpty(rels) || rels.size() != 1) {
            log.info("Department supervisor query failed, does not exist or multiple supervisors exist, department code: {}", department.getCode());
            return null;
        }
        return Models.origin().queryOneByWrapper(Pops.<PamirsEmployee>lambdaQuery()
                .from(PamirsEmployee.MODEL_MODEL)
                .eq(PamirsEmployee::getCode, rels.get(0).getEmployeeCode())
                .eq(PamirsEmployee::getEmployeeType, rels.get(0).getEmployeeType()));
    }

    @Function
    @Override
    public PamirsEmployee fillDepartmentDataByEmployee(PamirsEmployee employee) {
        if (CollectionUtils.isEmpty(employee.getDepartmentList())) {
            return employee;
        }
        List<PamirsDepartment> departments = employee.getDepartmentList();
        Map<String, PamirsDepartment> deptMap = departments.stream().collect(Collectors.toMap(PamirsDepartment::getCode, v -> v));
        List<DepartmentRelEmployee> rels = queryListByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getEmployeeCode, employee.getCode())
                .in(DepartmentRelEmployee::getDepartmentCode, deptMap.keySet())
        );
        if (CollectionUtils.isNotEmpty(rels)) {
            new DepartmentRelEmployee().listFieldQuery(rels, DepartmentRelEmployee::getImmediateSupervisor);
            for (DepartmentRelEmployee rel : rels) {
                PamirsDepartment department = deptMap.get(rel.getDepartmentCode());
                department.setImmediateSupervisor(rel.getImmediateSupervisor());
                department.setSupervisor(rel.getSupervisor());
            }
        }

        employee.setDepartmentList(departments);
        return employee;
    }

    @Function
    @Override
    public List<String> queryDeptCodeByEmpCode(String employeeCode) {
        List<DepartmentRelEmployee> rels = queryListByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getEmployeeCode, employeeCode)
                .select(DepartmentRelEmployee::getDepartmentCode)
        );
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        return rels.stream().map(DepartmentRelEmployee::getDepartmentCode).collect(Collectors.toList());
    }

    @Function
    @Override
    public Integer assignDepartmentSupervisor(String departmentCode, String employeeCode) {
        if (StringUtils.isEmpty(employeeCode)) {
            throw PamirsException.construct(BusinessExpEnumerate.EMPLOYEE_CODE).errThrow();
        }
        if (StringUtils.isEmpty(departmentCode)) {
            throw PamirsException.construct(BusinessExpEnumerate.DEPARTMENT_CODE_NOT_EMPTY_EXCEPTION).errThrow();
        }

        // 清空原主管
        clearSupervisor(Collections.singletonList(departmentCode));

        // 设置新主管
        DepartmentRelEmployee rel = new DepartmentRelEmployee().setSupervisor(true);
        IWrapper<DepartmentRelEmployee> assignSupervisorWrapper = Pops.<DepartmentRelEmployee>lambdaUpdate()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getDepartmentCode, departmentCode)
                .eq(DepartmentRelEmployee::getEmployeeCode, employeeCode);
        return rel.updateByWrapper(rel, assignSupervisorWrapper);
    }


    @Function
    @Override
    public void clearImmediateSupervisorByDept(PamirsDepartment data, List<PamirsEmployee> employeeList, List<PamirsEmployee> originEmployees) {
        Set<String> removeEmployeeCodes = originEmployees.stream().map(PamirsEmployee::getCode).collect(Collectors.toSet());
        for (PamirsEmployee employee : Optional.ofNullable(employeeList).orElse(Collections.emptyList())) {
            removeEmployeeCodes.remove(employee.getCode());
        }
        // 删除直属主管
        if (CollectionUtils.isNotEmpty(removeEmployeeCodes)) {
            DepartmentRelEmployee removeImmediateSupervisor = new DepartmentRelEmployee();
            removeImmediateSupervisor.setImmediateSupervisorCode(null);
            Models.data().updateByWrapper(removeImmediateSupervisor, Pops.<DepartmentRelEmployee>lambdaUpdate()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .eq(DepartmentRelEmployee::getDepartmentCode, data.getCode())
                    .in(DepartmentRelEmployee::getImmediateSupervisorCode, removeEmployeeCodes));
        }
    }

    @Function
    @Override
    public Integer assignSupervisorOrImmediateSupervisor(PamirsEmployee employee, List<PamirsDepartment> departmentList, List<PamirsDepartment> originDepartmentList) {
        if (CollectionUtils.isEmpty(departmentList)) {
            return 0;
        }

        String empCode = employee.getCode();
        String employeeType = employee.getEmployeeType();

        if (CollectionUtils.isNotEmpty(originDepartmentList)) {
            // 删除指定部门中该员工的直属主管关系
            removeImmediateSupervisorRelations(employee, departmentList, originDepartmentList);
        }

        List<String> clearSupervisorDeptCodes = new ArrayList<>();
        List<DepartmentRelEmployee> rels = new ArrayList<>();
        for (PamirsDepartment department : departmentList) {
            boolean supervisor = Boolean.TRUE.equals(department.getSupervisor());
            PamirsEmployee immediateSupervisor = department.getImmediateSupervisor();
            String immediateSupervisorCode = immediateSupervisor == null ? null : immediateSupervisor.getCode();
            DepartmentRelEmployee rel = new DepartmentRelEmployee();
            rel.setDepartmentType(department.getDepartmentType());
            rel.setDepartmentCode(department.getCode());
            rel.setEmployeeCode(empCode);
            rel.setEmployeeType(employeeType);
            rel.setSupervisor(supervisor);
            rel.setImmediateSupervisorCode(immediateSupervisorCode);
            rels.add(rel);
            if (supervisor) {
                clearSupervisorDeptCodes.add(department.getCode());
            }
        }
        // 直属主管环路检测
        checkImmediateSupervisor(rels);

        // 设置新主管前清除原主管
        if (!clearSupervisorDeptCodes.isEmpty()) {
            clearSupervisor(clearSupervisorDeptCodes);
        }
        return new DepartmentRelEmployee().createOrUpdateBatch(rels);
    }

    @Function
    @Override
    public void clearImmediateSupervisorCodes(Set<String> employeeCodes) {
        DepartmentRelEmployee update = new DepartmentRelEmployee().setImmediateSupervisorCode(null);
        new DepartmentRelEmployee().updateByWrapper(update, Pops.<DepartmentRelEmployee>lambdaUpdate()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .in(DepartmentRelEmployee::getImmediateSupervisorCode, employeeCodes)
        );
    }

    private void removeImmediateSupervisorRelations(PamirsEmployee employee, List<PamirsDepartment> departmentList, List<PamirsDepartment> originDepartmentList) {
        // 获取用户更新前的所有部门
        Set<String> removeDeptCodes = originDepartmentList.stream().map(PamirsDepartment::getCode).collect(Collectors.toSet());

        // 收集本次被删除的部门
        for (PamirsDepartment department : departmentList) {
            removeDeptCodes.remove(department.getCode());
        }

        // 3.删除直属主管
        if (!removeDeptCodes.isEmpty()) {
            DepartmentRelEmployee removeImmediateSupervisor = new DepartmentRelEmployee();
            removeImmediateSupervisor.setImmediateSupervisorCode(null);
            Models.data().updateByWrapper(removeImmediateSupervisor, Pops.<DepartmentRelEmployee>lambdaUpdate()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .in(DepartmentRelEmployee::getDepartmentCode, removeDeptCodes)
                    .eq(DepartmentRelEmployee::getImmediateSupervisorCode, employee.getCode()));
        }
    }

    private void clearSupervisor(List<String> departmentCodes) {
        DepartmentRelEmployee rel = new DepartmentRelEmployee();
        rel.setSupervisor(false);
        IWrapper<DepartmentRelEmployee> clearSupervisorWrapper = Pops.<DepartmentRelEmployee>lambdaUpdate()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .in(DepartmentRelEmployee::getDepartmentCode, departmentCodes)
                .eq(DepartmentRelEmployee::getSupervisor, true);
        rel.updateByWrapper(rel, clearSupervisorWrapper);
    }

    /**
     * 检测直属主管是否存在环路
     */
    private void checkImmediateSupervisor(List<DepartmentRelEmployee> relList) {
        Set<String> deptCodes = new HashSet<>();
        Map<String, String> deptMap = new HashMap<>();
        for (DepartmentRelEmployee rel : relList) {
            if (StringUtils.isNotBlank(rel.getImmediateSupervisorCode())) {
                deptCodes.add(rel.getDepartmentCode());
            }
            deptMap.put(rel.getDepartmentCode(), rel.getDepartmentType());
        }

        // 当前用户未设置直属主管，无需检测
        if (deptCodes.isEmpty()) {
            return;
        }

        // 1.收集涉及部门
        List<DepartmentRelEmployee> dbRelAll = Models.data().queryListByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .in(DepartmentRelEmployee::getDepartmentCode, deptCodes));

        // 2.构建员工->直属主管映射表
        Map<String/*部门编码*/, Map<String, String>> deptRelMap = new HashMap<>();
        dbRelAll.forEach(r -> putRelation(r, deptMap, deptRelMap));
        relList.forEach(r -> putRelation(r, deptMap, deptRelMap));

        // 3.直属主管环路检测
        for (Map.Entry<String, Map<String, String>> entry : deptRelMap.entrySet()) {
            String deptKey = entry.getKey();
            Map<String, String> relMap = entry.getValue();
            Set<String> visited = new HashSet<>();
            Set<String> visiting = new HashSet<>();
            for (String emp : relMap.keySet()) {
                if (detectCycle(emp, relMap, visited, visiting)) {
                    throw PamirsException.construct(BusinessExpEnumerate.DEPT_IMMEDIATE_SUPERVISOR_LOOP)
                            .appendMsg(deptKey)
                            .errThrow();
                }
            }
        }
    }

    private void putRelation(DepartmentRelEmployee r, Map<String, String> deptMap,
                                    Map<String, Map<String, String>> deptRelMap) {
        String deptType = deptMap.get(r.getDepartmentCode());
        if (StringUtils.isBlank(deptType) || !deptType.equals(r.getDepartmentType())) {
            return;
        }
        deptRelMap.computeIfAbsent(r.getDepartmentCode(), k -> new HashMap<>()).put(r.getEmployeeCode(), r.getImmediateSupervisorCode());
    }

    /**
     * 检测直属主管是否存在环路
     */
    private boolean detectCycle(String node, Map<String, String> rel, Set<String> visited, Set<String> visiting) {
        if (visited.contains(node)) {
            return false;
        }

        if (!visiting.add(node)) {
            return true;
        }

        // 向上递归遍历直属主管
        String parent = rel.get(node);
        if (parent != null && detectCycle(parent, rel, visited, visiting)) {
            return true;
        }

        visiting.remove(node);
        visited.add(node);
        return false;
    }
}
