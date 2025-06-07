package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.excel.EipExcel;
import pro.shushi.pamirs.eip.api.model.EipIntegrationFile;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * EipIntegrationFileService
 *
 * @author yakir on 2024/10/30 20:19.
 */
@Fun(EipIntegrationFileService.FUN_NAMESPACE)
public interface EipIntegrationFileService {

    String FUN_NAMESPACE = "pamirs.eip.EipIntegrationFileService";

    @Function
    EipIntegrationFile createOrUpdate(EipIntegrationFile data);

    @Function
    EipIntegrationFile queryById(Long id);

    @Function
    EipIntegrationFile queryByInterfaceName(String interfaceName);

    @Function
    EipExcel fetchData(String interfaceName, String sheet);

}
