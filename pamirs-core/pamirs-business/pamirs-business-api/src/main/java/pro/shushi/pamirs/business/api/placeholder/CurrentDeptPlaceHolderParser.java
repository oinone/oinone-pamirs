package pro.shushi.pamirs.business.api.placeholder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.session.DepartmentSession;
import pro.shushi.pamirs.business.api.spi.CurrentDepartmentFetcher;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;

/**
 * 当前用户所属部门编码占位符 {@code ${currentDept}}
 *
 * @author Adamancy Zhang at 17:24 on 2025-12-01
 */
@Component
public class CurrentDeptPlaceHolderParser extends AbstractPlaceHolderParser {

    private static final String PLACEHOLDER = "${currentDept}";

    @Override
    protected String value() {
        String departmentCode = DepartmentSession.getDepartmentCode();
        if (departmentCode == null) {
            PamirsDepartment department = CurrentDepartmentFetcher.get().fetch();
            if (department == null) {
                return EMPTY_CONDITION;
//                throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_DEPARTMENT_ERROR).errThrow();
            }
            departmentCode = department.getCode();
        }
        if (StringUtils.isBlank(departmentCode)) {
            return EMPTY_CONDITION;
//            throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_DEPARTMENT_ERROR).errThrow();
        }
        return departmentCode;
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
