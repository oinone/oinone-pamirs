package pro.shushi.pamirs.business.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.DomainChangeLog;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.core.base.utils.ProtectedDomain;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.sys.setting.api.CompanySettingsService;
import pro.shushi.pamirs.sys.setting.tmodel.CompanySettings;

import java.time.LocalDate;

/**
 * CompanySettingsServiceImpl
 *
 * @author yakir on 2022/09/20 12:11.
 */
@Slf4j
@Component
@Fun(CompanySettingsService.FUN_NAMESPACE)
public class CompanySettingsServiceImpl implements CompanySettingsService {

    @Function
    public Long domainChangeTimes(String companyCode) {
        if (StringUtils.isBlank(companyCode)) {
            return 3L;
        }
        int year = LocalDate.now().getYear();
        IWrapper<DomainChangeLog> qw = Pops.<DomainChangeLog>lambdaQuery()
                .from(DomainChangeLog.MODEL_MODEL)
                .eq(DomainChangeLog::getCompanyCode, companyCode)
                .eq(DomainChangeLog::getYear, year);

        Long changeCount = new DomainChangeLog().count(qw);
        return null == changeCount ? 0L : changeCount;
    }

    @Function
    public Long checkDomainUnique(String companyCode, String domain) {

        Boolean contains = ProtectedDomain.contains(domain);
        if (contains) {
            return 1L;
        }

        IWrapper<PamirsCompany> qw = Pops.<PamirsCompany>lambdaQuery()
                .from(PamirsCompany.MODEL_MODEL)
                .ne(PamirsCompany::getCode, companyCode)
                .eq(PamirsCompany::getDomainName, domain);

        return new PamirsCompany().count(qw);
    }

    @Function
    public CompanySettings queryOne(String companyCode) {

        CompanySettings settings = new CompanySettings();
        if (StringUtils.isBlank(companyCode)) {
            return settings;
        }
        PamirsCompany company = new PamirsCompany().queryByCode(companyCode);
        if (null == company) {
            return settings;
        }
        company = company.fieldQuery(PamirsCompany::getLogo);

        settings.setCompanyCode(companyCode);
        settings.setSearchable(company.getSearchable());

        settings.setLogo(company.getLogoUrl());

        settings.setTenant(PamirsTenantSession.getTenant());
        settings.setDomainName(company.getDomainName());
        return settings;
    }

    @Function
    @org.springframework.transaction.annotation.Transactional
    public CompanySettings save(CompanySettings data) {

        if (StringUtils.isBlank(data.getCompanyCode())) {
            return data;
        }

        PamirsCompany company = new PamirsCompany();
        company.setCode(data.getCompanyCode());
        PamirsCompany companyExisted = new PamirsCompany().queryByCode(data.getCompanyCode());
        if (StringUtils.isNotBlank(data.getDomainName()) && !StringUtils.equals(companyExisted.getDomainName(), data.getDomainName())) {
            long count = checkDomainUnique(data.getCompanyCode(), data.getDomainName());
            if (count > 0) {
                throw PamirsException.construct(BusinessExpEnumerate.COMPANY_DOMAIN_EXISTED)
                        .errThrow();
            }

            int year = LocalDate.now().getYear();
            IWrapper<DomainChangeLog> qw = Pops.<DomainChangeLog>lambdaQuery()
                    .from(DomainChangeLog.MODEL_MODEL)
                    .eq(DomainChangeLog::getCompanyCode, data.getCompanyCode())
                    .eq(DomainChangeLog::getYear, year);

            Long            changeCount     = new DomainChangeLog().count(qw);
            DomainChangeLog domainChangeLog = new DomainChangeLog();
            domainChangeLog.setCompanyCode(data.getCompanyCode());
            domainChangeLog.setYear(year);
            domainChangeLog.setDomain(data.getDomainName());
            domainChangeLog.create();

            company.setDomainName(data.getDomainName());
        }

        if (StringUtils.isNotBlank(data.getLogo())) {
            company.setLogoUrl(data.getLogo());
        }

        if (null != data.getSearchable()) {
            company.setSearchable(data.getSearchable());
        }

        company.updateByCode();

        return data;
    }
}
