package pro.shushi.pamirs.bizauth.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.bizauth.api.service.BusinessEmployeeService;
import pro.shushi.pamirs.bizauth.api.tmodel.PamirsEmployeeRoleTransient;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Model.model(PamirsEmployeeRoleTransient.MODEL_MODEL)
@Component
public class PamirsEmployeeRoleAction {

    @Autowired
    private BusinessEmployeeService businessEmployeeService;

    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API}, summary = "获取员工列表")
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "获取员工列表")
    public PamirsEmployeeRoleTransient construct(PamirsEmployeeRoleTransient data, List<PamirsEmployee> employeeList) {
        if (CollectionUtils.isEmpty(employeeList)) {
            return data;
        }
        //获取员工id列表
        List<Long> employeeIds = employeeList.stream().map(PamirsEmployee::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(employeeIds)) {
            return data;
        }
        //获取员工列表
        employeeList = Models.origin().queryListByWrapper(
                Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).in(PamirsEmployee::getId, employeeIds)
        );
        data.setEmployeeList(employeeList);
        return data;
    }

    @Action(displayName = "确定", summary = "修改员工角色")
    @Action.Advanced(type = FunctionTypeEnum.UPDATE)
    public PamirsEmployeeRoleTransient modifyEmployeeRole(PamirsEmployeeRoleTransient data) {
        businessEmployeeService.bindEmployeeRole(data.getRoleList(), data.getEmployeeList());
        return data;
    }
}
