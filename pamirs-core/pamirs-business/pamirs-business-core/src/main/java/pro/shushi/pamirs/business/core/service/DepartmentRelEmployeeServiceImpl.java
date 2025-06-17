package pro.shushi.pamirs.business.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
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

import java.util.Collections;
import java.util.List;
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
        List<DepartmentRelEmployee> rels = queryListByWrapper(Pops.<DepartmentRelEmployee>lambdaQuery()
                .from(DepartmentRelEmployee.MODEL_MODEL)
                .eq(DepartmentRelEmployee::getDepartmentCode, department.getCode())
                .eq(DepartmentRelEmployee::getDepartmentType, department.getDepartmentType())
                .eq(DepartmentRelEmployee::getSupervisor, Boolean.TRUE)
        );
        if (CollectionUtils.isEmpty(rels) || rels.size() != 1) {
            log.error("部门主管查询失败，不存在或存在多个主管，部门编码：{}", department.getCode());
            return null;
        }
        return Models.origin().queryOneByWrapper(Pops.<PamirsEmployee>lambdaQuery()
                .from(PamirsEmployee.MODEL_MODEL)
                .eq(PamirsEmployee::getCode, rels.get(0).getEmployeeCode())
                .eq(PamirsEmployee::getEmployeeType, rels.get(0).getEmployeeType()));
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
}
