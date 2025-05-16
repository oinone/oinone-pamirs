package pro.shushi.pamirs.business.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.enumeration.BindingModeEnum;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    @Function
    @Override
    public PamirsEmployee create(PamirsEmployee data) {
        return data.create();
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
            if (department!=null) {
                data.setDepartmentTreeCode(department.getTreeCode());
                if(CollectionUtils.isEmpty(departments)) departments = new ArrayList<>();
                if(departments.stream().noneMatch(t->t.getId().equals(department.getId()))){
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
            result = result.fieldSave(PamirsEmployee::getDepartmentList);
            result = result.fieldSave(PamirsEmployee::getPositions);
            return result;
        });
    }

    @Function
    @Override
    public PamirsEmployee update(PamirsEmployee data) {
        if (StringUtils.isBlank(data.getEmployeeType())) {
            data.setEmployeeType(BusinessModule.DEFAULT_TYPE);
        }
        List<PamirsPosition> positions = data.getPositions();
        List<PamirsDepartment> departmentList = data.getDepartmentList();
        if (CollectionUtils.isNotEmpty(positions)) {
            for (int i = 0; i < positions.size(); i++) {
                PamirsPosition position = positions.get(i).queryOne();
                positions.set(i, position);
            }
        }
        if (StringUtils.isNotBlank(data.getDepartmentCode())) {
            PamirsDepartment department = new PamirsDepartment().setCode(data.getDepartmentCode()).queryByCode();
            if (department!=null) {
                if(CollectionUtils.isEmpty(departmentList)) departmentList = new ArrayList<>();
                if(CollectionUtils.isEmpty(departmentList) || departmentList.stream().noneMatch(t->t.getCode().equals(department.getCode()))){
                    departmentList.add(department);
                    data.setDepartmentList(departmentList);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(departmentList)) {
            for (int i = 0; i < departmentList.size(); i++) {
                PamirsDepartment department = departmentList.get(i).queryOne();
                departmentList.set(i, department);
            }
        }

        if (StringUtils.isNotBlank(data.getDepartmentCode())) {
            PamirsDepartment department = new PamirsDepartment().setCode(data.getDepartmentCode()).queryByCode();
            if (department!=null) {
                data.setDepartmentTreeCode(department.getTreeCode());
            }
        }

        PamirsEmployee employee = data;
        List<PamirsDepartment> finalDepartmentList = departmentList;
        return Tx.build().execute(status -> {
            PamirsUser pamirsUser = null;
            if (employee.getBindingMode().equals(BindingModeEnum.CREATE_BINDING)) {
                pamirsUser = createUser(employee);
                createUserBaseRole(pamirsUser.getId());
            }
            PamirsEmployee result = this.updateById(employee);
            if (pamirsUser != null) {
                bindUserToEmployee(pamirsUser, result);
            }
            result = result.fieldQuery(PamirsEmployee::getDepartmentList);
            result = result.fieldQuery(PamirsEmployee::getPositions);
            result = result.relationDelete(PamirsEmployee::getDepartmentList);
            result = result.relationDelete(PamirsEmployee::getPositions);

            result.setPositions(positions);
            result.setDepartmentList(finalDepartmentList);
            result.fieldSave(PamirsEmployee::getDepartmentList);
            result.fieldSave(PamirsEmployee::getPositions);
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
        new PamirsEmployee().deleteByPks(list);
    }

    @Function
    @Override
    public void deleteById(PamirsEmployee data) {
        data.deleteById();
    }

    @Function
    @Override
    public Pagination<PamirsEmployee> queryPage(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper) {
        return new PamirsEmployee().queryPage(page, queryWrapper);
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
