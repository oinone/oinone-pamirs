package pro.shushi.pamirs.business.api.placeholder;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.session.EmployeeSession;
import pro.shushi.pamirs.business.api.spi.CurrentEmployeeFetcher;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 当前用户所属部门下的员工编码占位符 {@code ${currentEmployeeCodes}}
 *
 * @author Gesi at 9:46 on 2025/9/17
 */
@Component
public class CurrentDeptEmployeeCodesPlaceHolder extends AbstractPlaceHolderParser {

    private static final String PLACEHOLDER = "${currentDeptEmployeeCodes}";

    @Override
    protected String value() {
        Set<String> employeeCodes = EmployeeSession.getDeptEmployeeCodes();
        if (employeeCodes == null) {
            List<PamirsEmployee> employeeList = CurrentEmployeeFetcher.get().fetchDeptEmployeeList();
            if (CollectionUtils.isEmpty(employeeList)) {
                return EMPTY_CONDITION;
            }
            return CharacterConstants.LEFT_BRACKET +
                    employeeList.stream().map(PamirsEmployee::getCode).collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA)) +
                    CharacterConstants.RIGHT_BRACKET;
        }
        if (CollectionUtils.isEmpty(employeeCodes)) {
            return EMPTY_CONDITION;
        }
        return CharacterConstants.LEFT_BRACKET +
                String.join(CharacterConstants.SEPARATOR_COMMA, employeeCodes) +
                CharacterConstants.RIGHT_BRACKET;
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
