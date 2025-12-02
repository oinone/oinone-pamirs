package pro.shushi.pamirs.business.core.service;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.enumeration.BindingModeEnum;
import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.service.DepartmentRelEmployeeService;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.business.api.spi.CurrentDepartmentFetcher;
import pro.shushi.pamirs.business.api.spi.CurrentEmployeeFetcher;
import pro.shushi.pamirs.business.api.tmodel.PamirsEmployeeQueryFilter;
import pro.shushi.pamirs.business.util.DepartmentRelEmployeeHelper;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlParseHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.ux.common.utils.QueryHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link PamirsEmployeeService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsEmployeeService.FUN_NAMESPACE)
public class PamirsEmployeeServiceImpl implements PamirsEmployeeService {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRelEmployeeService departmentRelEmployeeService;

    @Function
    @Override
    public PamirsEmployee create(PamirsEmployee data) {
        if (StringUtils.isBlank(data.getEmployeeType())) {
            data.setEmployeeType(BusinessModule.DEFAULT_TYPE);
        }
        if (StringUtils.isNotBlank(data.getDepartmentCode())) {
            PamirsDepartment department = new PamirsDepartment().setCode(data.getDepartmentCode()).queryByCode();
            List<PamirsDepartment> departments = data.getDepartmentList();
            if (department != null) {
                data.setDepartmentTreeCode(department.getTreeCode());
                if (CollectionUtils.isEmpty(departments)) departments = new ArrayList<>();
                if (departments.stream().noneMatch(t -> t.getId().equals(department.getId()))) {
                    departments.add(department);
                    data.setDepartmentList(departments);
                }
            }
        }
        data = data.create();
        departmentRelEmployeeService.assignSupervisorOrImmediateSupervisor(data, data.getDepartmentList(), null);
        return data;
    }

    @Function
    @Override
    public PamirsEmployee createEmployeeAndUser(PamirsEmployee data) {
        if (StringUtils.isBlank(data.getEmployeeType())) {
            data.setEmployeeType(BusinessModule.DEFAULT_TYPE);
        }

        data.setDataStatus(DataStatusEnum.ENABLED);
        if (StringUtils.isNotBlank(data.getDepartmentCode())) {
            PamirsDepartment department = new PamirsDepartment().setCode(data.getDepartmentCode()).queryByCode();
            List<PamirsDepartment> departments = data.getDepartmentList();
            if (department != null) {
                data.setDepartmentTreeCode(department.getTreeCode());
                if (CollectionUtils.isEmpty(departments)) departments = new ArrayList<>();
                if (departments.stream().noneMatch(t -> t.getId().equals(department.getId()))) {
                    departments.add(department);
                    data.setDepartmentList(departments);
                }
            }
        }


        //TODO::


        PamirsEmployee employee = data;
        return Tx.build().execute(status -> {
            PamirsUser pamirsUser = createUser(employee);
            createUserBaseRole(pamirsUser.getId());
            PamirsEmployee result = this.create(employee);
            result = bindUserToEmployee(pamirsUser, result);
            result = result.fieldSave(PamirsEmployee::getPositions);
            result = result.fieldSave(PamirsEmployee::getDepartmentList);
            return result;
        });
    }

    @Function
    @Override
    public PamirsEmployee update(PamirsEmployee data) {
        if (StringUtils.isBlank(data.getEmployeeType())) {
            data.setEmployeeType(BusinessModule.DEFAULT_TYPE);
        }
        List<PamirsDepartment> departmentList = data.getDepartmentList();
        if (StringUtils.isNotBlank(data.getDepartmentCode())) {
            PamirsDepartment department = new PamirsDepartment().setCode(data.getDepartmentCode()).queryByCode();
            if (department != null) {
                if (CollectionUtils.isEmpty(departmentList)) departmentList = new ArrayList<>();
                if (CollectionUtils.isEmpty(departmentList) || departmentList.stream().noneMatch(t -> t.getCode().equals(department.getCode()))) {
                    departmentList.add(department);
                    data.setDepartmentList(departmentList);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(departmentList)) {
            List<String> deptCodes = departmentList.stream().map(PamirsDepartment::getCode).collect(Collectors.toList());
            List<PamirsDepartment> originDepartments = Models.data().queryListByWrapper(Pops.<PamirsDepartment>lambdaQuery()
                    .from(PamirsDepartment.MODEL_MODEL)
                    .in(PamirsDepartment::getCode, deptCodes));
            Map<String, PamirsDepartment> originDepartmentMap = originDepartments.stream()
                    .collect(Collectors.toMap(PamirsDepartment::getCode, v -> v));
            for (int i = 0; i < departmentList.size(); i++) {
                PamirsDepartment department = departmentList.get(i);
                PamirsDepartment originDepartment = originDepartmentMap.get(department.getCode());
                originDepartment.setSupervisor(department.getSupervisor());
                originDepartment.setImmediateSupervisor(department.getImmediateSupervisor());
                departmentList.set(i, originDepartment);
            }
        }

        if (StringUtils.isNotBlank(data.getDepartmentCode())) {
            PamirsDepartment department = new PamirsDepartment().setCode(data.getDepartmentCode()).queryByCode();
            if (department != null) {
                data.setDepartmentTreeCode(department.getTreeCode());
            }
        }

        PamirsEmployee employee = data;
        List<PamirsDepartment> finalDepartmentList = departmentList;
        return Tx.build().execute(status -> {
            PamirsUser pamirsUser = null;
            if (BindingModeEnum.CREATE_BINDING.equals(employee.getBindingMode())) {
                pamirsUser = createUser(employee);
                createUserBaseRole(pamirsUser.getId());
            }
            PamirsEmployee result = this.updateById(employee);
            if (pamirsUser != null) {
                bindUserToEmployee(pamirsUser, result);
            }
            result = result.fieldQuery(PamirsEmployee::getDepartmentList);
            List<PamirsDepartment> originDepartments = result.getDepartmentList();
            result = result.relationDelete(PamirsEmployee::getDepartmentList);

            result.setDepartmentList(finalDepartmentList);
            departmentRelEmployeeService.assignSupervisorOrImmediateSupervisor(data, result.getDepartmentList(), originDepartments);
            result.fieldSaveOnCascade(PamirsEmployee::getCompanyList);
            result.fieldSaveOnCascade(PamirsEmployee::getPositions);
            result.fieldSave(PamirsEmployee::getDepartmentList);
            return result;
        });
    }

    @Override
    public PamirsEmployee updateById(PamirsEmployee data) {
        data.updateById();
        return data;
    }

    @Function
    @Override
    public void deleteByPks(List<PamirsEmployee> list) {
        Set<String> employeeCodes = list.stream().map(PamirsEmployee::getCode).collect(Collectors.toSet());
        Tx.build().executeWithoutResult(status -> {
            new PamirsEmployee().deleteByWrapper(Pops.<PamirsEmployee>lambdaQuery()
                    .from(PamirsEmployee.MODEL_MODEL)
                    .in(PamirsEmployee::getCode, employeeCodes)
            );
            new DepartmentRelEmployee().deleteByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .in(DepartmentRelEmployee::getEmployeeCode, employeeCodes)
            );
            // 删除直属主管
            departmentRelEmployeeService.clearImmediateSupervisorCodes(employeeCodes);
        });
    }

    @Function
    @Override
    public void deleteById(PamirsEmployee data) {
        data = data.queryById();
        String employeeCode = data.getCode();
        data.deleteById();

        new DepartmentRelEmployee().deleteByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getEmployeeCode, employeeCode));
        // 删除直属主管
        departmentRelEmployeeService.clearImmediateSupervisorCodes(Sets.newHashSet(employeeCode));
    }

    @Function
    @Override
    public Pagination<PamirsEmployee> queryPage(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper) {
        return new PamirsEmployee().queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public Pagination<PamirsEmployee> queryPageAndFillSupervisor(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper) {
        Pagination<PamirsEmployee> pageResult = queryPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageResult.getContent())) {
            return pageResult;
        }

        // 从relationQuery结果中提取主管信息
        Map<String, Object> queryData = queryWrapper.getQueryData();
        if (queryData != null) {
            List<PamirsEmployee> employeeList = pageResult.getContent();
            DepartmentRelEmployeeHelper.fillSupervisorInfo(
                    employeeList,
                    queryWrapper.getQueryData(),
                    LambdaUtil.fetchFieldName(PamirsDepartment::getEmployeeList),
                    DepartmentRelEmployee::getEmployeeCode,
                    (employee, rel) -> employee.setSupervisor(Boolean.TRUE.equals(rel.getSupervisor()))
            );
        }
        return pageResult;
    }

    @Function
    @Override
    public PamirsEmployee queryOne(PamirsEmployee query) {
        return query.queryOne();
    }

    @Function
    @Override
    public List<AuthRole> queryEmployeeRoleListByUid(Long userId) {
        List<PamirsEmployee> employeeList = queryListByUid(userId);
        employeeList.stream().map(employee -> employee.fieldQuery(PamirsEmployee::getRoles)).collect(Collectors.toList());
        //不去重
        return employeeList.stream().map(PamirsEmployee::getRoles).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Function
    @Override
    public List<AuthRole> queryEmployeeRoleListByEmployeeId(Long employeeId) {
        PamirsEmployee employee = queryById(employeeId);
        employee.fieldQuery(PamirsEmployee::getRoles);
        return employee.getRoles();
    }

    @Function
    @Override
    public List<PamirsEmployee> queryListByUid(Long userId) {
        LambdaQueryWrapper<PamirsEmployee> wrapper = Pops.<PamirsEmployee>lambdaQuery().eq(PamirsEmployee::getBindingUserId, userId).from(PamirsEmployee.MODEL_MODEL);
        return new PamirsEmployee().queryList(wrapper);
    }

    @Function
    @Override
    public PamirsEmployee queryById(Long employeeId) {
        return new PamirsEmployee().queryById(employeeId);
    }

    @Function
    @Override
    public Pagination<PamirsEmployee> queryPageImmediateSupervisor(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper) {
        Map<String, Object> rsqlValues = RSQLHelper.getRsqlValues(queryWrapper.getOriginRsql(),
                PamirsEmployee::getDepartmentCode, PamirsEmployee::getName, PamirsEmployee::getCode);

        String departmentCode = (String) rsqlValues.get(LambdaUtil.fetchFieldName(PamirsEmployee::getDepartmentCode));
        String employeeName = (String) rsqlValues.get(LambdaUtil.fetchFieldName(PamirsEmployee::getName));
        String myselfCode = (String) rsqlValues.get(LambdaUtil.fetchFieldName(PamirsEmployee::getCode));

        List<String> employeeCode = Models.data().queryListByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getDepartmentCode, departmentCode)
                .select(DepartmentRelEmployee::getEmployeeCode)
        ).stream().map(DepartmentRelEmployee::getEmployeeCode).collect(Collectors.toList());
        if (employeeCode.isEmpty()) {
            return page;
        }

        // 获取直属主管列表
        Set<String> excludeCodes = fetchImmediateSupervisorCode(departmentCode, myselfCode);

        LambdaQueryWrapper<PamirsEmployee> query = Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL)
                .in(PamirsEmployee::getCode, employeeCode)
                .notIn(PamirsEmployee::getCode, excludeCodes);
        if (StringUtils.isNotBlank(employeeName)) {
            query.and(w -> w.like(StringUtils.isNotBlank(employeeName), PamirsEmployee::getName, employeeName)
                    .or().eq(StringUtils.isNotBlank(employeeName), PamirsEmployee::getCode, employeeName));
        }
        return Models.origin().queryPage(page, query);
    }

    @Function
    @Override
    public List<PamirsEmployee> queryListByDslFilter(PamirsEmployeeQueryFilter query) {
        String domainRsql = query.getDomain();
        List<String> employeeCodes = query.getEmployeeCodes();
        List<String> departmentCodes = query.getDepartmentCodes();
        List<String> roleCodes = query.getRoleCodes();
        Boolean userEmployee = query.getUserEmployee();
        Boolean userDept = query.getUserDept();
        Boolean userDeptAndChildren = query.getUserDeptAndChildren();

        LambdaQueryWrapper<PamirsEmployee> queryWrapper = Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL);
        if (StringUtils.isNotBlank(domainRsql)) {
            queryWrapper.apply(RsqlParseHelper.parseRsql2Sql(PamirsEmployee.MODEL_MODEL, domainRsql));
        }

        if (Boolean.TRUE.equals(userEmployee) || Boolean.TRUE.equals(userDept) || Boolean.TRUE.equals(userDeptAndChildren) || CollectionUtils.isNotEmpty(employeeCodes) || CollectionUtils.isNotEmpty(departmentCodes) || CollectionUtils.isNotEmpty(roleCodes)) {
            queryWrapper.and(andWrapper -> {
                andWrapper.from(PamirsEmployee.MODEL_MODEL);

                if (Boolean.TRUE.equals(userEmployee) || Boolean.TRUE.equals(userDept) || Boolean.TRUE.equals(userDeptAndChildren)) {
                    String childRsql = "";
                    if (Boolean.TRUE.equals(userEmployee)) {
                        PamirsEmployee employee = CurrentEmployeeFetcher.get().fetch();
                        if (employee != null) {
                            childRsql = LambdaUtil.fetchFieldName(PamirsEmployee::getCode) + "==" + employee.getCode();
                        }
                    }
                    if (Boolean.TRUE.equals(userDept) || Boolean.TRUE.equals(userDeptAndChildren)) {
                        if (Boolean.TRUE.equals(userDeptAndChildren)) {
                            List<PamirsDepartment> departments = CurrentDepartmentFetcher.get().fetchList();
                            if (CollectionUtils.isNotEmpty(departments)) {
                                childRsql = StringUtils.isNotBlank(childRsql) ? childRsql + " or " : "";
                                childRsql += (LambdaUtil.fetchFieldName(PamirsEmployee::getDepartmentTreeCode) + "=in= (\"" + departments.stream().map(PamirsDepartment::getCode).collect(Collectors.joining("\",\"")) + "\")");
                            }
                        } else {
                            PamirsDepartment department = CurrentDepartmentFetcher.get().fetch();
                            if (department != null) {
                                childRsql = StringUtils.isNotBlank(childRsql) ? childRsql + " or " : "";
                                childRsql += (LambdaUtil.fetchFieldName(PamirsEmployee::getDepartmentTreeCode) + "==" + department.getCode());
                            }
                        }
                    }
                    queryWrapper.apply(RsqlParseHelper.parseRsql2Sql(PamirsEmployee.MODEL_MODEL, childRsql));
                }

                if (CollectionUtils.isNotEmpty(employeeCodes)) {
                    andWrapper.or().in(PamirsEmployee::getCode, employeeCodes);
                }
                if (CollectionUtils.isNotEmpty(departmentCodes)) {
                    andWrapper.or().in(PamirsEmployee::getDepartmentCode, departmentCodes);
                }
                if (CollectionUtils.isNotEmpty(roleCodes)) {
                    List<Long> userIds = null;
                    List<Long> roleIds = new AuthRole().queryList(
                            Pops.<AuthRole>lambdaQuery().from(AuthRole.MODEL_MODEL)
                                    .select(AuthRole::getId)
                                    .in(AuthRole::getCode, roleCodes)
                    ).stream().map(AuthRole::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(roleIds)) {
                        userIds = new AuthUserRoleRel().queryList(
                                Pops.<AuthUserRoleRel>lambdaQuery().from(AuthUserRoleRel.MODEL_MODEL)
                                        .select(AuthUserRoleRel::getUserId)
                                        .in(AuthUserRoleRel::getRoleId, roleIds)
                        ).stream().map(AuthUserRoleRel::getUserId).distinct().collect(Collectors.toList());
                    }

                    if (CollectionUtils.isNotEmpty(userIds)) {
                        andWrapper.or().in(PamirsEmployee::getBindingUserId, userIds);
                    }
                }
            });
        }
        List<PamirsEmployee> employeeList = new ArrayList<>();
        QueryHelper.queryDataListByQueryPage(PamirsEmployee.MODEL_MODEL, queryWrapper, employeeList::addAll);
        return employeeList;
    }

    private Set<String> fetchImmediateSupervisorCode(String departmentCode, String myselfCode) {
        // 1.查询直属关系
        List<DepartmentRelEmployee> relList = Models.data().queryListByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getDepartmentCode, departmentCode)
                .isNotNull(DepartmentRelEmployee::getImmediateSupervisorCode)
                .select(DepartmentRelEmployee::getEmployeeCode, DepartmentRelEmployee::getImmediateSupervisorCode)
        );
        Map<String, String> empToSupervisor = relList.stream().collect(Collectors.toMap(
                DepartmentRelEmployee::getEmployeeCode,
                DepartmentRelEmployee::getImmediateSupervisorCode
        ));

        // 2.反向构建
        Map<String/*直属主管*/, List<String>/*下属*/> supervisor2Sub = new HashMap<>(empToSupervisor.size());
        empToSupervisor.forEach((subordinate, supervisor) -> {
            supervisor2Sub.computeIfAbsent(supervisor, k -> new ArrayList<>()).add(subordinate);
        });

        // 3.收集本人及所有下级员工
        Set<String> excludeCodes = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(myselfCode);
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            // 第一次加入才继续向下
            if (excludeCodes.add(cur)) {
                Collection<String> subs = supervisor2Sub.get(cur);
                if (subs != null) {
                    queue.addAll(subs);
                }
            }
        }
        return excludeCodes;
    }

    private void createUserBaseRole(Long userId) {
        AuthRole role = new AuthRole().setCode(AuthConstants.BUSINESS_BASE_CODE).queryOne();
        if (null == role) {
            throw new RuntimeException("未找到:" + AuthConstants.BUSINESS_BASE_ROLE);
        }
        AuthUserRoleRel userRoleRel = new AuthUserRoleRel();
        userRoleRel.setRoleId(role.getId());
        userRoleRel.setUserId(userId);
        userRoleRel.createOrUpdate();
    }

    private PamirsEmployee bindUserToEmployee(PamirsUser pamirsUser, PamirsEmployee pamirsEmployee) {
        PamirsEmployee update = new PamirsEmployee();
        update.setId(pamirsEmployee.getId());
        update.setBindingUserId(pamirsUser.getId());
        update.updateById();
        pamirsEmployee.setBindingUserId(pamirsUser.getId());
        return pamirsEmployee;
    }

    private PamirsUser createUser(PamirsEmployee data) {
        PamirsUser pamirsUser = new PamirsUser();
        pamirsUser.setNickname(data.getName());
        pamirsUser.setRealname(data.getName());
        pamirsUser.setName(data.getLogin());
        pamirsUser.setPhone(data.getPhone());
        pamirsUser.setEmail(data.getUserEmail());
        pamirsUser.setLogin(data.getLogin());
        pamirsUser.setInitialPassword(data.getInitialPassword());
        pamirsUser.setActive(Boolean.TRUE);
        pamirsUser.setSignUpType(UserSignUpType.BACKSTAGE);
        pamirsUser = userService.create(pamirsUser);

        return pamirsUser;
    }
}
