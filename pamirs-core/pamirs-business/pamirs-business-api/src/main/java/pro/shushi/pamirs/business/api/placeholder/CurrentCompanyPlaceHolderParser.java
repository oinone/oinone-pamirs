package pro.shushi.pamirs.business.api.placeholder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.session.CompanySession;
import pro.shushi.pamirs.business.api.spi.CurrentCompanyFetcher;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;

/**
 * 当前用户所属公司编码占位符 {@code ${currentCompany}}
 *
 * @author Adamancy Zhang at 12:19 on 2025-12-03
 */
@Component
public class CurrentCompanyPlaceHolderParser extends AbstractPlaceHolderParser {

    private static final String PLACEHOLDER = "${currentCompany}";

    @Override
    protected String value() {
        String companyCode = CompanySession.getCompanyCode();
        if (companyCode == null) {
            PamirsCompany company = CurrentCompanyFetcher.get().fetch();
            if (company == null) {
                return EMPTY_CONDITION;
//                throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_DEPARTMENT_ERROR).errThrow();
            }
            companyCode = company.getCode();
        }
        if (StringUtils.isBlank(companyCode)) {
            return EMPTY_CONDITION;
//            throw PamirsException.construct(BusinessExpEnumerate.USER_NOT_BINDING_DEPARTMENT_ERROR).errThrow();
        }
        return companyCode;
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
