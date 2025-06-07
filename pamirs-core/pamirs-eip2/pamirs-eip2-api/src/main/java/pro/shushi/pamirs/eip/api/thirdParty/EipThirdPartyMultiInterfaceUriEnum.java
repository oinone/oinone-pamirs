package pro.shushi.pamirs.eip.api.thirdParty;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.eip.api.constant.EipEnvironmentEnum;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;

/**
 * @author Adamancy Zhang on 2021-02-06 16:35
 */
public interface EipThirdPartyMultiInterfaceUriEnum {

    default String getName(EipEnvironmentEnum environment) {
        if (environment == null) {
            return null;
        }
        EipInterfaceUri interfaceUri = getInterfaceUri(environment);
        if (interfaceUri == null) {
            return null;
        }
        return interfaceUri.getName();
    }

    default String getInterfaceName(EipEnvironmentEnum environment) {
        if (environment == null) {
            return null;
        }
        EipInterfaceUri interfaceUri = getInterfaceUri(environment);
        if (interfaceUri == null) {
            return null;
        }
        return interfaceUri.getInterfaceName();
    }

    default String getUri(EipEnvironmentEnum environment) {
        if (environment == null) {
            return null;
        }
        EipInterfaceUri interfaceUri = getInterfaceUri(environment);
        if (interfaceUri == null) {
            return null;
        }
        return interfaceUri.getUri();
    }

    EipInterfaceUri getInterfaceUri(EipEnvironmentEnum environment);

    default EipIntegrationInterface newInstance(EipEnvironmentEnum environment, String module, String basePath) {
        return newInstance(environment, module, null, basePath, null);
    }

    default EipIntegrationInterface newInstance(EipEnvironmentEnum environment, String module, String nameSuffix, String basePath, String path) {
        EipInterfaceUri uri = this.getInterfaceUri(environment);
        StringBuilder uriBuilder = new StringBuilder(basePath).append(uri.getUri());
        if (StringUtils.isNotBlank(path)) {
            uriBuilder.append(URLHelper.repairAbsolutePath(path));
        }
        return new EipIntegrationInterface()
                .setUri(uriBuilder.toString())
                .setModule(module)
                .setName(uri.getName() + (StringUtils.isBlank(nameSuffix) ? "" : ("-" + nameSuffix)))
                .setInterfaceName(uri.getInterfaceName())
                .construct();
    }
}
