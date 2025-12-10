package pro.shushi.pamirs.business.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.runtime.executor.DataPermissionExecutor;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.service.DepartmentRelEmployeeService;
import pro.shushi.pamirs.business.api.service.PamirsDepartmentService;
import pro.shushi.pamirs.business.api.spi.CurrentCompanyFetcher;
import pro.shushi.pamirs.business.api.spi.CurrentDepartmentFetcher;
import pro.shushi.pamirs.business.api.tmodel.DepartmentQueryFilter;
import pro.shushi.pamirs.business.util.DepartmentRelEmployeeHelper;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.behavior.impl.TreeCodeBehavior;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlParseHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link PamirsDepartmentService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsDepartmentService.FUN_NAMESPACE)
public class PamirsDepartmentServiceImpl implements PamirsDepartmentService {

    @Autowired
    private DepartmentRelEmployeeService departmentRelEmployeeService;

    @Function
    @Override
    public PamirsDepartment create(PamirsDepartment data) {
        if (StringUtils.isBlank(data.getCompanyCode())) {
            throw PamirsException.construct(BusinessExpEnumerate.COMPANY_CODE_EMPTY).errThrow();
        }
        if (null == data.getId()) {
            data.setPriority(Instant.now().toEpochMilli());
        }
        data.setPriority(Instant.now().toEpochMilli());

        if (StringUtils.isBlank(data.getDepartmentType())) {
            data.setDepartmentType(BusinessModule.DEFAULT_TYPE);
        }
        if (StringUtils.isBlank(data.getCode())) {
            data.setCode(CommonApiFactory.<String>getSequenceGenerator().generate(SequenceEnum.SEQ.value(), PamirsDepartment.MODEL_MODEL));
        }
        if (StringUtils.isNotBlank(data.getParentCode())) {
            PamirsDepartment dbDepartment = new PamirsDepartment().setCode(data.getParentCode()).queryByCode();
            if (StringUtils.isNotBlank(dbDepartment.getTreeCode())) {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getTreeCode(), data.getCode()));
            } else {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getCode(), data.getCode()));
            }
        } else {
            data.setTreeCode(data.getCode());
        }

        if (CollectionUtils.isNotEmpty(data.getPositionList())) {
            data.getPositionList().forEach(K2::construct);
        }
        data.fieldSave(PamirsDepartment::getPositionList);
        List<PamirsEmployee> employees = data.getEmployeeList();
        if (CollectionUtils.isNotEmpty(employees)) {
            data.fieldSave(PamirsDepartment::getEmployeeList);
            // 设置部门主管
            assignDepartmentSupervisor(data, employees);
        }
        return data.create();
    }

    @Function
    @Override
    public PamirsDepartment queryOne(PamirsDepartment data) {
        PamirsDepartment department = data.queryById(data.getId());
        if (department == null) {
            return data;
        }
        department = department.fieldQuery(PamirsDepartment::getParent);
        department = department.fieldQuery(PamirsDepartment::getCompany);
        department = department.fieldQuery(PamirsDepartment::getPositionList);
        department = department.fieldQuery(PamirsDepartment::getEmployeeList);
        return department;
    }

    @Function
    @Override
    public void update(PamirsDepartment data) {
        PamirsDepartment exist = this.queryOne(data);
        if (StringUtils.isNotBlank(data.getParentCode())) {
            PamirsDepartment dbDepartment = new PamirsDepartment().setCode(data.getParentCode()).queryByCode();
            if (StringUtils.isNotBlank(dbDepartment.getTreeCode())) {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getTreeCode(), data.getCode()));
            } else {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getCode(), data.getCode()));
            }
        } else {
            data.setTreeCode(data.getCode());
        }
        exist = exist.fieldQuery(PamirsDepartment::getEmployeeList);
        List<PamirsEmployee> originEmployees = exist.getEmployeeList();
        Tx.build().executeWithoutResult(status -> {
            data.updateById();
            data.fieldSaveOnCascade(PamirsDepartment::getPositionList);
            data.fieldSaveOnCascade(PamirsDepartment::getEmployeeList);
            if (CollectionUtils.isNotEmpty(data.getEmployeeList())) {
                assignDepartmentSupervisor(data, data.getEmployeeList());
                departmentRelEmployeeService.clearImmediateSupervisorByDept(data, data.getEmployeeList(), originEmployees);
            }
        });
    }

    @Function
    @Override
    public void deleteByPk(PamirsDepartment data) {
        data.deleteByPk();
    }

    @Function
    @Override
    public void deleteByPks(List<PamirsDepartment> list) {
        Set<String> deptCodes = list.stream().map(PamirsDepartment::getCode).collect(Collectors.toSet());
        Tx.build().executeWithoutResult(status -> {
            new PamirsDepartment().deleteByWrapper(Pops.<PamirsDepartment>lambdaQuery()
                    .from(PamirsDepartment.MODEL_MODEL)
                    .in(PamirsDepartment::getCode, deptCodes));
            new DepartmentRelEmployee().deleteByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                    .from(DepartmentRelEmployee.MODEL_MODEL)
                    .in(DepartmentRelEmployee::getDepartmentCode, deptCodes));
        });
    }

    @Function
    @Override
    public Pagination<PamirsDepartment> queryPage(Pagination<PamirsDepartment> page, IWrapper<PamirsDepartment> queryWrapper) {
        return new PamirsDepartment().queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public Pagination<PamirsDepartment> queryPageAndFillSupervisor(Pagination<PamirsDepartment> page, IWrapper<PamirsDepartment> queryWrapper) {
        Pagination<PamirsDepartment> pageResult = queryPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageResult.getContent())) {
            return pageResult;
        }

        // 从relationQuery结果中提取主管信息
        Map<String, Object> queryData = queryWrapper.getQueryData();
        if (queryData != null) {
            List<PamirsDepartment> departmentList = pageResult.getContent();
            boolean fillResult = DepartmentRelEmployeeHelper.fillSupervisorInfo(
                    departmentList,
                    queryWrapper.getQueryData(),
                    LambdaUtil.fetchFieldName(PamirsEmployee::getDepartmentList),
                    DepartmentRelEmployee::getDepartmentCode,
                    (department, rel) -> {
                        department.setSupervisor(Boolean.TRUE.equals(rel.getSupervisor()));
                        department.setImmediateSupervisor(rel.getImmediateSupervisor());
                    }
            );
            if (fillResult) {
                departmentList.sort(Comparator.comparing(PamirsDepartment::getSupervisor, Comparator.reverseOrder()));
            }
        }

        return pageResult;
    }

    private void assignDepartmentSupervisor(PamirsDepartment data, List<PamirsEmployee> employees) {
        for (PamirsEmployee employeeItem : employees) {
            if (Boolean.TRUE.equals(employeeItem.getSupervisor())) {
                departmentRelEmployeeService.assignDepartmentSupervisor(data.getCode(), employeeItem.getCode());
                break;
            }
        }
    }

    @Function
    @Override
    public PamirsDepartment fillDeptSupervisor(PamirsDepartment department) {
        List<PamirsEmployee> employees = department.getEmployeeList();
        if (CollectionUtils.isEmpty(employees)) {
            return department;
        }
        PamirsEmployee supervisorEmployee = departmentRelEmployeeService.queryDepartmentSupervisor(department);
        if (supervisorEmployee == null) {
            return department;
        }

        // 设置部门主管，并将部门主管移动到数组最前方
        int supervisorIndex = -1;
        for (int i = 0; i < employees.size(); i++) {
            PamirsEmployee employeeItem = employees.get(i);
            if (employeeItem.getCode().equals(supervisorEmployee.getCode())) {
                employeeItem.setSupervisor(true);
                supervisorIndex = i;
                break;
            }
        }
        if (supervisorIndex > 0) {
            Collections.swap(employees, 0, supervisorIndex);
        }
        return department;
    }

    @Function
    @Override
    public List<PamirsDepartment> queryListByFilter(DepartmentQueryFilter query) {
        Set<String> departmentCodes = new HashSet<>();
        Optional.ofNullable(query.getDepartmentCodes()).filter(CollectionUtils::isNotEmpty).ifPresent(departmentCodes::addAll);
        boolean isReturnEmpty = false;
        if (Boolean.TRUE.equals(query.getUserCompanyDept())) {
            isReturnEmpty = true;
            List<PamirsCompany> companyList = CurrentCompanyFetcher.get().fetchList();
            if (CollectionUtils.isNotEmpty(companyList)) {
                List<PamirsDepartment> departments = Models.origin().queryListByWrapper(Pops.<PamirsDepartment>lambdaQuery()
                        .from(PamirsDepartment.MODEL_MODEL)
                        .select(PamirsDepartment::getId, PamirsDepartment::getCode)
                        .in(PamirsDepartment::getCompanyCode, companyList.stream().map(PamirsCompany::getCode).collect(Collectors.toSet())));
                if (CollectionUtils.isNotEmpty(departments)) {
                    departmentCodes.addAll(departments.stream().map(PamirsDepartment::getCode).collect(Collectors.toSet()));
                }
            }
        } else if (Boolean.TRUE.equals(query.getUserDeptAndChildren())) {
            isReturnEmpty = true;
            List<PamirsDepartment> departments = CurrentDepartmentFetcher.get().fetchListWithChildren();
            if (CollectionUtils.isNotEmpty(departments)) {
                departmentCodes.addAll(departments.stream().map(PamirsDepartment::getCode).collect(Collectors.toSet()));
            }
        } else if (Boolean.TRUE.equals(query.getUserDept())) {
            isReturnEmpty = true;
            List<PamirsDepartment> departments = CurrentDepartmentFetcher.get().fetchList();
            if (CollectionUtils.isNotEmpty(departments)) {
                departmentCodes.addAll(departments.stream().map(PamirsDepartment::getCode).collect(Collectors.toSet()));
            }
        }
        String rsql = query.getRsql();
        String filter = DataPermissionExecutor.getFilter(PamirsDepartment.MODEL_MODEL, FunctionConstants.queryPage, Lists.newArrayList(FunctionTypeEnum.QUERY));
        if (StringUtils.isNotBlank(filter)) {
            if (StringUtils.isBlank(rsql)) {
                rsql = filter;
            } else {
                rsql = String.format("(%s) and (%s)", rsql, filter);
            }
        }
        String applySql = null;
        if (StringUtils.isNotBlank(rsql)) {
            applySql = RsqlParseHelper.parseRsql2Sql(PamirsDepartment.MODEL_MODEL, rsql);
            isReturnEmpty = false;
        }
        if (isReturnEmpty && departmentCodes.isEmpty()) {
            return new ArrayList<>();
        }
        List<PamirsDepartment> departmentList;
        final String finalApplySql = applySql;
        if (departmentCodes.isEmpty() || departmentCodes.size() <= 500) {
            LambdaQueryWrapper<PamirsDepartment> queryWrapper = Pops.<PamirsDepartment>lambdaQuery().from(PamirsDepartment.MODEL_MODEL);
            if (finalApplySql != null) {
                queryWrapper.apply(finalApplySql);
            }
            if (!departmentCodes.isEmpty()) {
                queryWrapper.in(PamirsDepartment::getCode, departmentCodes);
            }
            departmentList = Models.origin().queryListByWrapper(queryWrapper);
        } else {
            departmentList = DataShardingHelper.build().collectionSharding(departmentCodes, (sublist) -> {
                LambdaQueryWrapper<PamirsDepartment> queryWrapper = Pops.<PamirsDepartment>lambdaQuery().from(PamirsDepartment.MODEL_MODEL)
                        .setBatchSize(-1)
                        .in(PamirsDepartment::getCode, sublist);
                if (finalApplySql != null) {
                    queryWrapper.apply(finalApplySql);
                }
                return Models.origin().queryListByWrapper(queryWrapper);
            });
        }
        Map<String, PamirsDepartment> flatMap = new LinkedHashMap<>();
        fullParentDepartment(departmentList, flatMap);
        return new ArrayList<>(flatMap.values());
    }

    private void fullParentDepartment(List<PamirsDepartment> departmentList, Map<String, PamirsDepartment> flatMap) {
        Set<String> parentCodes = new HashSet<>();
        for (PamirsDepartment department : departmentList) {
            flatMap.put(department.getCode(), department);
            String parentCode = department.getParentCode();
            if (StringUtils.isNotBlank(parentCode)) {
                parentCodes.add(parentCode);
            }
        }
        parentCodes = new HashSet<>(Sets.difference(parentCodes, flatMap.keySet()));
        if (CollectionUtils.isEmpty(parentCodes)) {
            return;
        }
        List<PamirsDepartment> parentDepartmentList = Models.origin().queryListByWrapper(Pops.<PamirsDepartment>lambdaQuery().from(PamirsDepartment.MODEL_MODEL).in(PamirsDepartment::getCode, parentCodes));
        if (CollectionUtils.isEmpty(departmentList)) {
            return;
        }
        fullParentDepartment(parentDepartmentList, flatMap);
    }
}
