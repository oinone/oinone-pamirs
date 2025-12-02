package pro.shushi.pamirs.business.api.placeholder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.session.EmployeeSession;
import pro.shushi.pamirs.business.api.spi.CurrentEmployeeFetcher;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;

/**
 * 当前用户员工编码占位符 {@code ${currentEmployee}}
 *
 * @author Gesi at 9:46 on 2025/9/17
 */
@Component
public class CurrentEmployeePlaceHolderParser extends AbstractPlaceHolderParser {

    private static final String PLACEHOLDER = "${currentEmployee}";

    @Override
    protected String value() {
        String employeeCode = EmployeeSession.getEmployeeCode();
        if (employeeCode == null) {
            PamirsEmployee employee = CurrentEmployeeFetcher.get().fetch();
            if (employee == null) {
                return EMPTY_CONDITION;
//                throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_EMPLOYEE_ERROR).errThrow();
            }
            employeeCode = employee.getCode();
        }
        if (StringUtils.isBlank(employeeCode)) {
            return EMPTY_CONDITION;
//            throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_EMPLOYEE_ERROR).errThrow();
        }
        return employeeCode;
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
        return PLACEHOLDER;
    }
}
