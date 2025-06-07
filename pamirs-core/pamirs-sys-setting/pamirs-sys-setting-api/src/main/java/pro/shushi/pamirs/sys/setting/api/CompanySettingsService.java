package pro.shushi.pamirs.sys.setting.api;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.sys.setting.tmodel.CompanySettings;

/**
 * CompanySettingsService
 *
 * @author yakir on 2022/09/20 11:59.
 */
@Fun(CompanySettingsService.FUN_NAMESPACE)
public interface CompanySettingsService {

    String FUN_NAMESPACE = "core.sys.setting.CompanySettingsService";

    @Function
    Long domainChangeTimes(String companyCode);

    @Function
    Long checkDomainUnique(String companyCode, String domain);

    @Function
    CompanySettings queryOne(String companyCode);

    @Function
    CompanySettings save(CompanySettings data);

}
