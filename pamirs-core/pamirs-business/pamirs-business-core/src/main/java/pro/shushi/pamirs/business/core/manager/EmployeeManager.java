package pro.shushi.pamirs.business.core.manager;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR;

/**
 * @author Gesi at 9:45 on 2025/9/17
 */
@Slf4j
@Component
public class EmployeeManager {

    public String currentUserEmpCode() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            throw PamirsException.construct(BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }

        return userEmpCode(userId);
    }

    public String userEmpCode(Long userId) {
        List<PamirsEmployee> employeeList = new PamirsEmployee()
                .queryList(Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).eq(PamirsEmployee::getBindingUserId, userId));
        return employeeList.stream().map(PamirsEmployee::getCode).collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA));
    }

}
