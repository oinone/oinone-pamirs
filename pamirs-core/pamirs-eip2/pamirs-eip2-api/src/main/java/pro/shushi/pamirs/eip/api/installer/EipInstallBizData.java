package pro.shushi.pamirs.eip.api.installer;

import pro.shushi.pamirs.eip.api.model.*;
import pro.shushi.pamirs.eip.api.model.config.EipSingletonConfig;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorAuth;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorResource;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * EipInstallBizData
 *
 * @author yakir on 2024/06/25 18:33.
 */
@Data
public class EipInstallBizData {

    private List<EipConnGroup> groupList;
    private List<EipLib> eipLibList;
    private List<EipApplication> eipAppList;
    private List<EipOpenInterface> openApiList;
    private List<EipIntegrate> integrateList;
    private List<EipSingletonConfig> eipConfigList;
    private List<EipIntegrationInterface> iiList;
    private List<EipIntegrationFile> ifList;
    private List<EipConnector> connList;
    private List<EipConnectorResource> connResList;
    private List<EipConnectorAuth> authList;
}
