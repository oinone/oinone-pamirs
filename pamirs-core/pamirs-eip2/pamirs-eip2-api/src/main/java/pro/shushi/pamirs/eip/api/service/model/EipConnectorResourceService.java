package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipCallParam;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorResource;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * EIP连接器资源服务
 *
 * @author Adamancy Zhang at 21:46 on 2025-02-26
 */
@Fun(EipConnectorResourceService.FUN_NAMESPACE)
public interface EipConnectorResourceService {

    String FUN_NAMESPACE = "eip.EipConnectorResourceService";

    @Function
    EipConnectorResource queryByInterfaceName(String interfaceName);

    @Function
    EipConnectorResource prepareCall(EipConnectorResource resource, List<EipCallParam> parameters);

}
