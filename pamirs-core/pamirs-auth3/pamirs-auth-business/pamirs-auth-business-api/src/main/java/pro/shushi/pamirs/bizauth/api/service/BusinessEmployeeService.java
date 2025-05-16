package pro.shushi.pamirs.bizauth.api.service;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(BusinessEmployeeService.FUN_NAMESPACE)
public interface BusinessEmployeeService {

    public static final String FUN_NAMESPACE = "pamirs.authbusiness.api.BusinessEmployeeService";

    @Function
    Boolean bindEmployeeRole(List<AuthRole> roleList, List<PamirsEmployee> employeeList);
}
