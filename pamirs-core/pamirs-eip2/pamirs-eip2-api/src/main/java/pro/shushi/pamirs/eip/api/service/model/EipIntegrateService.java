package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipIntegrate;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * EipIntegrateService
 *
 * @author yakir on 2023/04/12 16:39.
 */
@Fun(EipIntegrateService.FUN_NAMESPACE)
public interface EipIntegrateService {

    String FUN_NAMESPACE = "eip.EipIntegrateService";

    EipIntegrate createOrUpdate(EipIntegrate data);

    EipIntegrate queryOne(EipIntegrate data);

    EipIntegrate delete(EipIntegrate data);

    EipIntegrate changeStatus(EipIntegrate data);
}
