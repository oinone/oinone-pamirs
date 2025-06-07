package pro.shushi.pamirs.business.view.pojo;

import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsPosition;

import java.util.Map;

/**
 * @author Wuxin
 * @Date 2024/6/28
 * @since 1.0
 */
public class PositionPojo {
    private Map<String, PamirsCompany> companyHashMap;
    private Map<String, PamirsDepartment> departmentHashMap;
    private Map<String, PamirsPosition> pamirsPositionMap;


    public void setCompanyHashMap(Map<String, PamirsCompany> companyHashMap) {
        this.companyHashMap = companyHashMap;
    }

    public void setDepartmentHashMap(Map<String, PamirsDepartment> departmentHashMap) {
        this.departmentHashMap = departmentHashMap;
    }

    public void setPamirsPositionMap(Map<String, PamirsPosition> pamirsPositionMap) {
        this.pamirsPositionMap = pamirsPositionMap;
    }

    public Map<String, PamirsCompany> getCompanyHashMap() {
        return companyHashMap;
    }

    public Map<String, PamirsDepartment> getDepartmentHashMap() {
        return departmentHashMap;
    }

    public Map<String, PamirsPosition> getPamirsPositionMap() {
        return pamirsPositionMap;
    }

    public static PositionPojo of(Map<String, PamirsCompany> companyHashMap, Map<String, PamirsDepartment> departmentHashMap, Map<String, PamirsPosition> pamirsPositionMap) {
        PositionPojo positionPojo = new PositionPojo();

        positionPojo.setCompanyHashMap(companyHashMap);
        positionPojo.setDepartmentHashMap(departmentHashMap);
        positionPojo.setPamirsPositionMap(pamirsPositionMap);
        return positionPojo;
    }
}
