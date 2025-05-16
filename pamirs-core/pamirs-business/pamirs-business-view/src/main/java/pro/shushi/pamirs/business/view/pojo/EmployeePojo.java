package pro.shushi.pamirs.business.view.pojo;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Wuxin
 * @Date 2024/6/28
 * @since 1.0
 */
public class EmployeePojo {
    private Map<String, PamirsCompany> companyHashMap;
    private Map<String, PamirsDepartment> departmentHashMap;
    private Map<String, AuthRole> authRoleHashMap;
    private Map<String, PamirsEmployee> pamirsEmployeeMap;

    public Map<String, PamirsEmployee> getPamirsEmployeeMap() {
        return pamirsEmployeeMap;
    }

    public void setPamirsEmployeeMap(Map<String, PamirsEmployee> pamirsEmployeeMap) {
        this.pamirsEmployeeMap = pamirsEmployeeMap;
    }

    public Map<String, PamirsCompany> getCompanyHashMap() {
        return companyHashMap;
    }

    public void setCompanyHashMap(Map<String, PamirsCompany> companyHashMap) {
        this.companyHashMap = companyHashMap;
    }

    public Map<String, PamirsDepartment> getDepartmentHashMap() {
        return departmentHashMap;
    }

    public void setDepartmentHashMap(Map<String, PamirsDepartment> departmentHashMap) {
        this.departmentHashMap = departmentHashMap;
    }

    public Map<String, AuthRole> getAuthRoleHashMap() {
        return authRoleHashMap;
    }

    public void setAuthRoleHashMap(Map<String, AuthRole> authRoleHashMap) {
        this.authRoleHashMap = authRoleHashMap;
    }

    public static EmployeePojo of(Map<String, PamirsCompany> companyHashMap,
                                  Map<String, PamirsDepartment> departmentHashMap,
                                  Map<String, AuthRole> authRoleHashMap) {

        EmployeePojo employeePojo = new EmployeePojo();
        employeePojo.setCompanyHashMap(companyHashMap);
        employeePojo.setDepartmentHashMap(departmentHashMap);
        employeePojo.setAuthRoleHashMap(authRoleHashMap);
        employeePojo.setPamirsEmployeeMap(new HashMap<>());
        return employeePojo;
    }
}
