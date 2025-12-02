package pro.shushi.pamirs.business.api.spi.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.session.CompanySession;
import pro.shushi.pamirs.business.api.session.EmployeeSession;
import pro.shushi.pamirs.business.api.spi.CurrentCompanyFetcher;
import pro.shushi.pamirs.business.api.spi.CurrentEmployeeFetcher;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 获取当前公司默认实现
 *
 * @author Adamancy Zhang at 12:32 on 2025-12-02
 */
@Order
@Component
@SPI.Service
public class DefaultCurrentCompanyFetcher implements CurrentCompanyFetcher {

    @Override
    public PamirsCompany fetch() {
        String companyCode = getCurrentCompanyCodeBySession();
        if (StringUtils.isBlank(companyCode)) {
            companyCode = getCurrentCompanyCodeByEmployee();
            if (StringUtils.isBlank(companyCode)) {
                return null;
            }
        }
        return Models.origin().queryOneByWrapper(Pops.<PamirsCompany>lambdaQuery()
                .from(PamirsCompany.MODEL_MODEL)
                .select(PamirsCompany::getId, PamirsCompany::getCode)
                .eq(PamirsCompany::getCode, companyCode));
    }

    protected String getCurrentCompanyCodeBySession() {
        String companyCode = CompanySession.getCompanyCode();
        if (StringUtils.isNotBlank(companyCode)) {
            return companyCode;
        }
        String employeeCode = EmployeeSession.getEmployeeCode();
        if (StringUtils.isBlank(employeeCode)) {
            return null;
        }
        PamirsEmployee employee = Models.origin().queryOneByWrapper(Pops.<PamirsEmployee>lambdaQuery()
                .from(PamirsEmployee.MODEL_MODEL)
                .select(PamirsEmployee::getId, PamirsEmployee::getCompanyCode)
                .eq(PamirsEmployee::getCode, employeeCode));
        if (employee == null) {
            return null;
        }
        return employee.getCompanyCode();
    }

    protected String getCurrentCompanyCodeByEmployee() {
        PamirsEmployee employee = CurrentEmployeeFetcher.get().fetch();
        if (employee == null) {
            return null;
        }
        return employee.getCompanyCode();
    }
}
