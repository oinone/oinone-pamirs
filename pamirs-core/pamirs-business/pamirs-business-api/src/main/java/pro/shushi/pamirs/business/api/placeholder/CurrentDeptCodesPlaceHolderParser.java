package pro.shushi.pamirs.business.api.placeholder;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.session.DepartmentSession;
import pro.shushi.pamirs.business.api.spi.CurrentDepartmentFetcher;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 当前用户所属部门编码及子部门编码占位符 {@code ${currentDeptCodes}}
 *
 * @author Adamancy Zhang at 19:14 on 2025-12-01
 */
@Component
public class CurrentDeptCodesPlaceHolderParser extends AbstractPlaceHolderParser {

    private static final String PLACEHOLDER = "${currentDeptCodes}";

    @Override
    protected String value() {
        Set<String> departmentCodes = DepartmentSession.getDepartmentCodes();
        if (departmentCodes == null) {
            List<PamirsDepartment> departments = CurrentDepartmentFetcher.get().fetchList();
            if (CollectionUtils.isEmpty(departments)) {
                return EMPTY_CONDITION;
//                throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_DEPARTMENT_ERROR).errThrow();
            }
            return CharacterConstants.LEFT_BRACKET +
                    departments.stream().map(PamirsDepartment::getCode).collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA)) +
                    CharacterConstants.RIGHT_BRACKET;
        }
        if (CollectionUtils.isEmpty(departmentCodes)) {
            return EMPTY_CONDITION;
//            throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_DEPARTMENT_ERROR).errThrow();
        }
        return CharacterConstants.LEFT_BRACKET +
                String.join(CharacterConstants.SEPARATOR_COMMA, departmentCodes) +
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
