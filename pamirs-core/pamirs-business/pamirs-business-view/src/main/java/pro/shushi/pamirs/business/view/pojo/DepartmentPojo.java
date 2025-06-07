package pro.shushi.pamirs.business.view.pojo;

import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Wuxin
 * @Date 2024/6/28
 * @since 1.0
 */
public class DepartmentPojo {
    private Map<String, PamirsCompany> companyHashMap;
    private Map<String, PamirsDepartment> codeByDepartmentHashMap;
    private Map<String, PamirsDepartment> codeByDepartmentMapExcel;
    private Stack<PamirsDepartment> priorityStack;

    public Map<String, PamirsCompany> getCompanyHashMap() {
        return companyHashMap;
    }

    public void setCompanyHashMap(Map<String, PamirsCompany> companyHashMap) {
        this.companyHashMap = companyHashMap;
    }

    public Map<String, PamirsDepartment> getCodeByDepartmentHashMap() {
        return codeByDepartmentHashMap;
    }

    public void setCodeByDepartmentHashMap(Map<String, PamirsDepartment> codeByDepartmentHashMap) {
        this.codeByDepartmentHashMap = codeByDepartmentHashMap;
    }

    public Map<String, PamirsDepartment> getCodeByDepartmentMapExcel() {
        return codeByDepartmentMapExcel;
    }

    public void setCodeByDepartmentMapExcel(Map<String, PamirsDepartment> codeByDepartmentMapExcel) {
        this.codeByDepartmentMapExcel = codeByDepartmentMapExcel;
    }

    public Stack<PamirsDepartment> getPriorityStack() {
        return priorityStack;
    }

    public void setPriorityStack(Stack<PamirsDepartment> priorityStack) {
        this.priorityStack = priorityStack;
    }

    public static DepartmentPojo of(Map<String, PamirsCompany> companyHashMap, Map<String, PamirsDepartment> codeByDepartmentHashMap) {
        DepartmentPojo departmentPojo = new DepartmentPojo();
        departmentPojo.setCompanyHashMap(companyHashMap);
        departmentPojo.setCodeByDepartmentHashMap(codeByDepartmentHashMap);
        departmentPojo.setCodeByDepartmentMapExcel(new HashMap<>());
        departmentPojo.setPriorityStack(new Stack<>());
        return departmentPojo;
    }
}
