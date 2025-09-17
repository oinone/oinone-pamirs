package pro.shushi.pamirs.business.placeholder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.core.manager.EmployeeManager;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;

/**
 * @author Gesi at 9:46 on 2025/9/17
 */
@Component
public class EmployeesPlaceHolder extends AbstractPlaceHolderParser {

    private static final String EMPLOYEE_CODE_PLACEHOLDER = "${currentUserEmpCodes}";

    @Autowired
    private EmployeeManager employeeManager;

    @Override
    protected String value() {
        return employeeManager.currentUserEmpCodes();
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
        return EMPLOYEE_CODE_PLACEHOLDER;
    }
}
