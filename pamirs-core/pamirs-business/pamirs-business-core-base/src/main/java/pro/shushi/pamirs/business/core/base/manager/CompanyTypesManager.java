package pro.shushi.pamirs.business.core.base.manager;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.enumeration.EntType;
import pro.shushi.pamirs.business.api.enumeration.GovType;
import pro.shushi.pamirs.business.api.enumeration.OrgType;
import pro.shushi.pamirs.business.api.tmodel.CompanyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * CompanyTypesManager
 *
 * @author yakir on 2022/09/13 19:41.
 */
@Component
public class CompanyTypesManager {

    private final List<CompanyType> entTypes = new ArrayList<>();
    private final List<CompanyType> govTypes = new ArrayList<>();
    private final List<CompanyType> orgTypes = new ArrayList<>();

    public CompanyTypesManager() {
        initEntTypes();
        initGovTypes();
        initOrgTypes();
    }

    public List<CompanyType> entTypes() {
        return this.entTypes;
    }

    public List<CompanyType> govTypes() {
        return this.govTypes;
    }

    public List<CompanyType> orgTypes() {
        return this.orgTypes;
    }

    private void initEntTypes() {
        Map<String, CompanyType> parentMap = new HashMap<>();
        for (EntType entType : EntType.values()) {
            CompanyType companyType = CompanyType.instance(entType);
            companyType.setDept(entType.getDepth());
            if (0 == entType.getDepth() && null == entType.getParent()) {
                parentMap.put(entType.getValue(), companyType);
            } else {
                CompanyType parent = parentMap.get(entType.getParent().getValue());
                companyType.setParent(parent);
                companyType.setParentType(parent.getType());
            }
            entTypes.add(companyType);
        }
    }

    private void initGovTypes() {
        Map<String, CompanyType> parentMap = new HashMap<>();
        for (GovType govType : GovType.values()) {
            CompanyType companyType = CompanyType.instance(govType);
            companyType.setDept(govType.getDepth());
            if (0 == govType.getDepth() && null == govType.getParent()) {
                parentMap.put(govType.getValue(), companyType);
            } else {
                CompanyType parent = parentMap.get(govType.getParent().getValue());
                companyType.setParent(parent);
                companyType.setParentType(parent.getType());
            }
            govTypes.add(companyType);
        }
    }

    private void initOrgTypes() {

        for (OrgType orgType : OrgType.values()) {
            if (OrgType.DEFAULT_TYPE.equals(orgType)) {
                continue;
            }
            CompanyType companyType = CompanyType.instance(orgType).setDept(0);
            orgTypes.add(companyType);
        }
    }
}
