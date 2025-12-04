package pro.shushi.pamirs.business.api.spi.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.session.CompanySession;
import pro.shushi.pamirs.business.api.spi.CurrentCompanyFetcher;
import pro.shushi.pamirs.business.api.spi.CurrentEmployeeFetcher;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
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
        String companyCode = CompanySession.getCompanyCode();
        if (StringUtils.isBlank(companyCode)) {
            companyCode = getCurrentCompanyCodeByEmployee();
            if (StringUtils.isBlank(companyCode)) {
                return null;
            }
        }
        return Models.origin().queryOneByWrapper(generatorWrapper().eq(PamirsCompany::getCode, companyCode));
    }

    protected String getCurrentCompanyCodeByEmployee() {
        PamirsEmployee employee = CurrentEmployeeFetcher.get().fetch();
        if (employee == null) {
            return null;
        }
        return employee.getCompanyCode();
    }

    protected LambdaQueryWrapper<PamirsCompany> generatorWrapper() {
        return Pops.<PamirsCompany>lambdaQuery()
                .from(PamirsCompany.MODEL_MODEL)
                .select(PamirsCompany::getId, PamirsCompany::getCode)
                .eq(PamirsCompany::getDataStatus, DataStatusEnum.ENABLED);
    }
}
