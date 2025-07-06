package pro.shushi.pamirs.business.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.pmodel.DepartmentRelEmployeeProxy;
import pro.shushi.pamirs.business.api.service.DepartmentRelEmployeeProxyService;
import pro.shushi.pamirs.business.api.service.PamirsDepartmentService;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.connector.RSQLNodeConnector;
import pro.shushi.pamirs.framework.gateways.rsql.visitor.NormalRSQLParseVisitor;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.business.api.pmodel.DepartmentRelEmployeeProxy.DEFAULT_CODE;
import static pro.shushi.pamirs.business.api.pmodel.DepartmentRelEmployeeProxy.MODEL_MODEL;

/**
 * @author yeshenyue on 2025/5/27 17:02.
 */
@Slf4j
@Service
@Fun(DepartmentRelEmployeeProxyService.FUN_NAMESPACE)
public class DepartmentRelEmployeeProxyServiceImpl implements DepartmentRelEmployeeProxyService {

    @Autowired
    private PamirsEmployeeService pamirsEmployeeService;
    @Autowired
    private PamirsDepartmentService pamirsDepartmentService;

    private static final String FIELD_EMPLOYEE_NAME = LambdaUtil.fetchFieldName(DepartmentRelEmployeeProxy::getEmployeeName);
    private static final String FIELD_EMPLOYEE_CODE = LambdaUtil.fetchFieldName(DepartmentRelEmployeeProxy::getEmployeeCode);
    private static final String FIELD_COMPANY_CODE = LambdaUtil.fetchFieldName(DepartmentRelEmployeeProxy::getCompanyCode);

    @Function
    @Override
    public Pagination<DepartmentRelEmployeeProxy> queryCanEmployeeChoose(Pagination<DepartmentRelEmployeeProxy> page, IWrapper<DepartmentRelEmployeeProxy> queryWrapper) {
        String rsql = queryWrapper.getOriginRsql();

        List<Long> excludedIds = getListByRSql(rsql, DepartmentRelEmployeeProxy::getEmployeeId)
                .stream().map(Long::valueOf).collect(Collectors.toList());
        Map<String, Object> rsqlValues = RSQLHelper.getRsqlValues(rsql, DepartmentRelEmployeeProxy::getCompanyCode,
                DepartmentRelEmployeeProxy::getEmployeeCode);

        Map<String, Object> queryData = queryWrapper.getQueryData();
        String employeeName = (String) queryData.get(FIELD_EMPLOYEE_NAME);

        IWrapper<PamirsEmployee> wrapper = Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL)
                .isNotNull(PamirsEmployee::getEmployeeType)
                .eq(PamirsEmployee::getCompanyCode, rsqlValues.get(FIELD_COMPANY_CODE))
                .like(StringUtils.isNotBlank(employeeName), PamirsEmployee::getName, employeeName)
                .eq(rsqlValues.containsKey(FIELD_EMPLOYEE_CODE), PamirsEmployee::getCode, rsqlValues.get(FIELD_EMPLOYEE_CODE))
                .notIn(CollectionUtils.isNotEmpty(excludedIds), PamirsEmployee::getId, excludedIds);

        Pagination<PamirsEmployee> pageQuery = ArgUtils.convert(MODEL_MODEL, PamirsEmployee.MODEL_MODEL, page);
        Pagination<PamirsEmployee> dbPage = pamirsEmployeeService.queryPage(pageQuery, wrapper);

        List<PamirsEmployee> employeeList = dbPage.getContent();
        List<DepartmentRelEmployeeProxy> resultContents = new ArrayList<>(employeeList.size());
        if (CollectionUtils.isNotEmpty(employeeList)) {
            for (PamirsEmployee employee : employeeList) {
                DepartmentRelEmployeeProxy content = new DepartmentRelEmployeeProxy();
                content.setEmployee(employee);
                content.setDepartmentType(BusinessModule.DEFAULT_TYPE);
                content.setDepartmentCode(DEFAULT_CODE);
                content.setEmployeeCode(employee.getCode());
                content.setEmployeeType(employee.getEmployeeType());
                resultContents.add(content);
            }
        }

        Pagination<DepartmentRelEmployeeProxy> result = ArgUtils.convert(PamirsEmployee.MODEL_MODEL, MODEL_MODEL, dbPage);
        result.setContent(resultContents);
        return page;
    }

    @Override
    @Function
    public Pagination<DepartmentRelEmployeeProxy> queryCanDepartmentChoose(Pagination<DepartmentRelEmployeeProxy> page, IWrapper<DepartmentRelEmployeeProxy> queryWrapper) {
        String rsql = queryWrapper.getOriginRsql();

        Map<String, Object> rsqlValues = RSQLHelper.getRsqlValues(rsql, DepartmentRelEmployeeProxy::getCompanyCode,
                DepartmentRelEmployeeProxy::getEmployeeName,
                DepartmentRelEmployeeProxy::getEmployeeCode);
        List<Long> excludedIds = getListByRSql(rsql, DepartmentRelEmployeeProxy::getDepartmentId)
                .stream().map(Long::valueOf).collect(Collectors.toList());
        Map<String, Object> queryData = queryWrapper.getQueryData();
        String departmentName = (String) queryData.get(LambdaUtil.fetchFieldName(DepartmentRelEmployeeProxy::getDepartmentName));
        String departmentCode = (String) queryData.get(LambdaUtil.fetchFieldName(DepartmentRelEmployeeProxy::getDepartmentCode));

        Pagination<PamirsDepartment> pageQuery = ArgUtils.convert(MODEL_MODEL, PamirsEmployee.MODEL_MODEL, page);
        Pagination<PamirsDepartment> dbPage = pamirsDepartmentService.queryPage(pageQuery, Pops.<PamirsDepartment>lambdaQuery()
                .from(PamirsDepartment.MODEL_MODEL)
                .isNotNull(PamirsDepartment::getDepartmentType)
                .eq(PamirsDepartment::getCompanyCode, rsqlValues.get(FIELD_COMPANY_CODE))
                .like(StringUtils.isNotBlank(departmentName), PamirsDepartment::getName, departmentName)
                .eq(StringUtils.isNotBlank(departmentCode), PamirsDepartment::getCode, departmentCode)
                .notIn(CollectionUtils.isNotEmpty(excludedIds), PamirsDepartment::getId, excludedIds));

        List<PamirsDepartment> departmentList = dbPage.getContent();
        List<DepartmentRelEmployeeProxy> resultContents = new ArrayList<>(departmentList.size());
        if (CollectionUtils.isNotEmpty(departmentList)) {
            for (PamirsDepartment department : departmentList) {
                DepartmentRelEmployeeProxy content = new DepartmentRelEmployeeProxy();
                content.setDepartment(department);
                content.setEmployeeType(BusinessModule.DEFAULT_TYPE);
                content.setEmployeeCode(DEFAULT_CODE);
                content.setDepartmentCode(department.getCode());
                content.setDepartmentType(department.getDepartmentType());
                resultContents.add(content);
            }
        }

        Pagination<DepartmentRelEmployeeProxy> result = ArgUtils.convert(PamirsEmployee.MODEL_MODEL, MODEL_MODEL, dbPage);
        result.setContent(resultContents);
        return page;
    }

    private static <T> List<String> getListByRSql(String rsql, Getter<T, ?> getter) {
        if (StringUtils.isBlank(rsql)) {
            return Collections.emptyList();
        }
        TreeNode<RSQLNodeInfo> root = RSQLHelper.parse(rsql, new NormalRSQLParseVisitor<>(), null);
        if (root == null) {
            return Collections.emptyList();
        }
        String fieldName = LambdaUtil.fetchFieldName(getter);
        List<String> result = new ArrayList<>();
        RSQLHelper.toTargetString(root, new RSQLNodeConnector() {
            @Override
            public String comparisonConnector(RSQLNodeInfo nodeInfo) {
                if (fieldName.contains(nodeInfo.getField())) {
                    result.addAll(nodeInfo.getArguments());
                }
                return super.comparisonConnector(nodeInfo);
            }
        });
        return result;
    }

    @Function
    @Override
    public void updateRelationDepartment(PamirsDepartment department) {
        List<DepartmentRelEmployeeProxy> relList = department.getEmployeeRelList();
        if (relList == null) {
            return;
        }

        // 查询部门下所有员工
        Map<String/*empCode*/, String/*empType*/> emptyImmMap = queryExistingEmployeeRelations(department);

        // 填充字段并收集部门下被删除的员工
        for (DepartmentRelEmployee rel : relList) {
            rel.setDepartmentCode(department.getCode());
            rel.setDepartmentType(department.getDepartmentType());
            emptyImmMap.remove(rel.getEmployeeCode());
        }

        if (!emptyImmMap.isEmpty()) {
            DepartmentRelEmployee updateImmediate = new DepartmentRelEmployee();
            updateImmediate.setImmediateSupervisorCode(null);

            // 删除直属主管
            Models.data().updateByWrapper(updateImmediate, Pops.<DepartmentRelEmployee>lambdaUpdate()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .eq(DepartmentRelEmployee::getDepartmentCode, department.getCode())
                    .eq(DepartmentRelEmployee::getDepartmentType, department.getDepartmentType())
                    .in(DepartmentRelEmployee::getImmediateSupervisorCode, emptyImmMap.keySet())
            );

            // 删除部门下的员工关联关系
            Models.data().deleteByWrapper(Pops.<DepartmentRelEmployee>lambdaUpdate()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .eq(DepartmentRelEmployee::getDepartmentCode, department.getCode())
                    .eq(DepartmentRelEmployee::getDepartmentType, department.getDepartmentType())
                    .and(inner -> emptyImmMap.forEach((code, type) -> inner
                            .or(sub -> sub.eq(DepartmentRelEmployee::getEmployeeCode, code)
                                    .eq(DepartmentRelEmployee::getEmployeeType, type)))));
        }

        Models.data().createOrUpdateBatch(relList);
    }

    private Map<String, String> queryExistingEmployeeRelations(PamirsDepartment department) {
        IWrapper<DepartmentRelEmployee> query = Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getDepartmentCode, department.getCode())
                .eq(DepartmentRelEmployee::getDepartmentType, department.getDepartmentType())
                .select(DepartmentRelEmployee::getEmployeeCode, DepartmentRelEmployee::getEmployeeType);
        List<DepartmentRelEmployee> rels = Models.data().queryListByWrapper(query);
        if (CollectionUtils.isEmpty(rels)) {
            return Collections.emptyMap();
        }
        return rels.stream().collect(Collectors.toMap(DepartmentRelEmployee::getEmployeeCode, DepartmentRelEmployee::getEmployeeType));
    }

    @Override
    @Function
    public void updateRelationEmployee(PamirsEmployee employee) {
        List<DepartmentRelEmployeeProxy> relList = employee.getDepartmentRelList();
        if (relList == null) {
            return;
        }

        // 1.查询用户下所有部门
        Set<String> removeDeptCodes = queryOriginalDepartmentCodes(employee);

        // 2.填充字段并收集本次被删除的部门
        List<DepartmentRelEmployeeProxy> supervisorRelList = new ArrayList<>();
        for (DepartmentRelEmployeeProxy rel : relList) {
            rel.setEmployeeCode(employee.getCode());
            rel.setEmployeeType(employee.getEmployeeType());
            if (Boolean.TRUE.equals(rel.getSupervisor())) {
                supervisorRelList.add(rel);
            }
            if (rel.getImmediateSupervisor() == null) {
                rel.setImmediateSupervisorCode(null);
            }
            removeDeptCodes.remove(rel.getDepartmentCode());
        }

        // 3.检测直属主管，防止形成闭环
        checkImmediateSupervisor(relList);

        // 4.当前用户为部门主管，取消原部门主管
        if (!supervisorRelList.isEmpty()) {
            Set<String> deptCodes = supervisorRelList.stream().map(DepartmentRelEmployeeProxy::getDepartmentCode).collect(Collectors.toSet());
            Set<String> deptTypes = supervisorRelList.stream().map(DepartmentRelEmployeeProxy::getDepartmentType).collect(Collectors.toSet());
            DepartmentRelEmployee update = new DepartmentRelEmployee().setSupervisor(false);
            update.updateByWrapper(update, Pops.<DepartmentRelEmployee>lambdaUpdate()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .in(DepartmentRelEmployee::getDepartmentCode, deptCodes)
                    .in(DepartmentRelEmployee::getDepartmentType, deptTypes));
        }

        // 5.删除直属主管
        if (!removeDeptCodes.isEmpty()) {
            DepartmentRelEmployee removeImmediateSupervisor = new DepartmentRelEmployee();
            removeImmediateSupervisor.setImmediateSupervisorCode(null);
            Models.data().updateByWrapper(removeImmediateSupervisor, Pops.<DepartmentRelEmployee>lambdaUpdate()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .in(DepartmentRelEmployee::getDepartmentCode, removeDeptCodes)
                    .eq(DepartmentRelEmployee::getImmediateSupervisorCode, employee.getCode()));
        }

        // 6.更新/删除关联关系
        Set<String> deptCode = relList.stream().map(DepartmentRelEmployeeProxy::getDepartmentCode).collect(Collectors.toSet());
        LambdaUpdateWrapper<DepartmentRelEmployee> deleteWrapper = Pops.<DepartmentRelEmployee>lambdaUpdate()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getEmployeeCode, employee.getCode())
                .eq(DepartmentRelEmployee::getEmployeeType, employee.getEmployeeType());
        if (!deptCode.isEmpty()) {
            deleteWrapper.notIn(DepartmentRelEmployee::getDepartmentCode, deptCode);
        }
        Models.data().createOrUpdateBatch(relList);
        Models.data().deleteByWrapper(deleteWrapper);
    }

    /**
     * 查询员工原有的部门编码
     */
    private Set<String> queryOriginalDepartmentCodes(PamirsEmployee employee) {
        IWrapper<DepartmentRelEmployee> query = Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getEmployeeCode, employee.getCode())
                .eq(DepartmentRelEmployee::getEmployeeType, employee.getEmployeeType())
                .select(DepartmentRelEmployee::getDepartmentCode);
        List<DepartmentRelEmployee> rels = Models.data().queryListByWrapper(query);
        return rels.stream().map(DepartmentRelEmployee::getDepartmentCode).collect(Collectors.toSet());
    }

    /**
     * 检测直属主管是否存在环路
     */
    private void checkImmediateSupervisor(List<DepartmentRelEmployeeProxy> relList) {
        Set<String> deptCodes = new HashSet<>();
        Map<String, String> deptMap = new HashMap<>();
        for (DepartmentRelEmployeeProxy rel : relList) {
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

    private static void putRelation(DepartmentRelEmployee r, Map<String, String> deptMap,
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
