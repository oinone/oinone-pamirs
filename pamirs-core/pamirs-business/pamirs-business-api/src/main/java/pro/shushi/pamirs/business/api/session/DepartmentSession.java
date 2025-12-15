package pro.shushi.pamirs.business.api.session;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 当前部门会话上下文
 *
 * @author Adamancy Zhang at 17:53 on 2025-12-01
 */
public class DepartmentSession {

    private static final String ID_KEY = "CURRENT_DEPARTMENT_ID";

    private static final String CODE_KEY = "CURRENT_DEPARTMENT_CODE";

    private static final String TREE_CODE_KEY = "CURRENT_DEPARTMENT_TREE_CODE";

    private static final String TYPE_KEY = "CURRENT_DEPARTMENT_TYPE";

    private static final String CODES_KEY = "CURRENT_DEPARTMENT_CODES";

    private static final String CODES_WITH_CHILDREN_KEY = "CURRENT_DEPARTMENT_CODES_WITH_CHILDREN";

    public static String getDepartmentId() {
        return PamirsSession.getTransmittableExtend().get(ID_KEY);
    }

    public static void setDepartmentId(Long employeeId) {
        PamirsSession.getTransmittableExtend().put(ID_KEY, String.valueOf(employeeId));
    }

    public static String getDepartmentCode() {
        return PamirsSession.getTransmittableExtend().get(CODE_KEY);
    }

    public static void setDepartmentCode(String code) {
        PamirsSession.getTransmittableExtend().put(CODE_KEY, code);
    }

    public static String getDepartmentTreeCode() {
        return PamirsSession.getTransmittableExtend().get(TREE_CODE_KEY);
    }

    public static void setDepartmentTreeCode(String treeCode) {
        PamirsSession.getTransmittableExtend().put(TREE_CODE_KEY, treeCode);
    }

    public static String getDepartmentType() {
        return PamirsSession.getTransmittableExtend().get(TYPE_KEY);
    }

    public static void setDepartmentType(String type) {
        PamirsSession.getTransmittableExtend().put(TYPE_KEY, type);
    }

    public static Set<String> getDepartmentCodes() {
        String value = PamirsSession.getTransmittableExtend().get(CODES_KEY);
        if (value == null) {
            return null;
        }
        if (CharacterConstants.SEPARATOR_EMPTY.equals(value)) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(Arrays.asList(value.split(CharacterConstants.SEPARATOR_COMMA)));
    }

    public static void setDepartmentCodes(Set<String> codes) {
        PamirsSession.getTransmittableExtend().put(CODES_KEY, String.join(CharacterConstants.SEPARATOR_COMMA, codes));
    }

    public static Set<String> getDepartmentCodesWithChildren() {
        String value = PamirsSession.getTransmittableExtend().get(CODES_WITH_CHILDREN_KEY);
        if (value == null) {
            return null;
        }
        if (CharacterConstants.SEPARATOR_EMPTY.equals(value)) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(Arrays.asList(value.split(CharacterConstants.SEPARATOR_COMMA)));
    }

    public static void setDepartmentCodesWithChildren(Set<String> codes) {
        PamirsSession.getTransmittableExtend().put(CODES_WITH_CHILDREN_KEY, String.join(CharacterConstants.SEPARATOR_COMMA, codes));
    }
}
