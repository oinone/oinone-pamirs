package pro.shushi.pamirs.business.placeholder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.core.manager.DepartmentManager;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;

@Component
public class DepartmentWithChildsPlaceHolder extends AbstractPlaceHolderParser {

    // 返回当前用户组织部门的【树编码】
    private static final String DEPARTMENT_CODE_PLACEHOLDER = "${currentUserDeptWithChildCodes}";

    @Autowired
    private DepartmentManager departmentManager;

    @Override
    protected String value() {
        return departmentManager.currentUserDeptWithChildCodes();
    }

    @Override
    public Integer priority() {
        return -namespace().length();
    }

    @Override
    public Boolean active() {
        return true;
    }

    @Override
    public String namespace() {
        return DEPARTMENT_CODE_PLACEHOLDER;
    }
}
