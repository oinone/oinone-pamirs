package pro.shushi.pamirs.business.api.session;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 当前员工会话上下文
 *
 * @author Adamancy Zhang at 17:30 on 2025-12-01
 */
public class EmployeeSession {

    private static final String ID_KEY = "CURRENT_EMPLOYEE_ID";

    private static final String CODE_KEY = "CURRENT_EMPLOYEE_CODE";

    private static final String TYPE_KEY = "CURRENT_EMPLOYEE_TYPE";

    private static final String DEPT_EMPLOYEE_CODES_KEY = "CURRENT_DEPT_EMPLOYEE_CODES";

    private static final String DEPT_WITH_CHILDREN_EMPLOYEE_CODES_KEY = "CURRENT_DEPT_WITH_CHILDREN_EMPLOYEE_CODES";

    public static String getEmployeeId() {
        return PamirsSession.getTransmittableExtend().get(ID_KEY);
    }

    public static void setEmployeeId(Long id) {
        PamirsSession.getTransmittableExtend().put(ID_KEY, String.valueOf(id));
    }

    public static String getEmployeeCode() {
        return PamirsSession.getTransmittableExtend().get(CODE_KEY);
    }

    public static void setEmployeeCode(String code) {
        PamirsSession.getTransmittableExtend().put(CODE_KEY, code);
    }

    public static String getEmployeeType() {
        return PamirsSession.getTransmittableExtend().get(TYPE_KEY);
    }

    public static void setEmployeeType(String type) {
        PamirsSession.getTransmittableExtend().put(TYPE_KEY, type);
    }

    public static Set<String> getDeptEmployeeCodes() {
        String value = PamirsSession.getTransmittableExtend().get(DEPT_EMPLOYEE_CODES_KEY);
        if (value == null) {
            return null;
        }
        if (CharacterConstants.SEPARATOR_EMPTY.equals(value)) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(Arrays.asList(value.split(CharacterConstants.SEPARATOR_COMMA)));
    }

    public static void setDeptEmployeeCodes(Set<String> codes) {
        PamirsSession.getTransmittableExtend().put(DEPT_EMPLOYEE_CODES_KEY, String.join(CharacterConstants.SEPARATOR_COMMA, codes));
    }

    public static Set<String> getDeptWithChildrenEmployeeCodes() {
        String value = PamirsSession.getTransmittableExtend().get(DEPT_WITH_CHILDREN_EMPLOYEE_CODES_KEY);
        if (value == null) {
            return null;
        }
        if (CharacterConstants.SEPARATOR_EMPTY.equals(value)) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(Arrays.asList(value.split(CharacterConstants.SEPARATOR_COMMA)));
    }

    public static void setDeptWithChildrenEmployeeCodes(Set<String> codes) {
        PamirsSession.getTransmittableExtend().put(DEPT_WITH_CHILDREN_EMPLOYEE_CODES_KEY, String.join(CharacterConstants.SEPARATOR_COMMA, codes));
    }
}
