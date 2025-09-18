package pro.shushi.pamirs.business.core.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR;

/**
 * @author Gesi at 9:45 on 2025/9/17
 */
@Component
public class EmployeeManager {

    public String currentUserEmpCode() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            throw PamirsException.construct(BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }

        return userEmpCode(userId);
    }

    public String currentUserEmpCodes() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            throw PamirsException.construct(BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }

        return userEmpCodes(userId);
    }

    public String userEmpCode(Long userId) {
        List<PamirsEmployee> employeeList = new PamirsEmployee()
                .queryList(Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).select(PamirsEmployee::getCode).eq(PamirsEmployee::getBindingUserId, userId));
        if (CollectionUtils.isEmpty(employeeList)) {
            return "";
        }
        return employeeList.get(0).getCode();
    }

    public String userEmpCodes(Long userId) {
        List<PamirsEmployee> employeeList = new PamirsEmployee()
                .queryList(Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).select(PamirsEmployee::getCode).eq(PamirsEmployee::getBindingUserId, userId));
        String employeeCodes = employeeList.stream().map(PamirsEmployee::getCode).map(i -> "'" + i + "'").collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA));
        return StringUtils.isNotBlank(employeeCodes) ? "(" + employeeCodes + ")" : "''";
    }

}
