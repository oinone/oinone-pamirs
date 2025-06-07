package pro.shushi.pamirs.eip.api.thirdParty;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;

/**
 * <h>Eip 第三方接口uri枚举接口</h>
 * <p>
 * name: 请使用可识别名称进行定义
 * interfaceName: 请使用第三方平台名称前缀+下划线+接口名称进行定义
 * uri: 请使用根路径方式定义
 * </p>
 *
 * @author Adamancy Zhang on 2021-02-04 14:32
 */
public interface EipThirdPartyInterfaceUriEnum {

    String getName();

    String getInterfaceName();

    String getUri();

    default EipIntegrationInterface newInstance(String module, String basePath) {
        return newInstance(module, null, basePath, null);
    }

    default EipIntegrationInterface newInstance(String module, String nameSuffix, String basePath, String path) {
        StringBuilder uriBuilder = new StringBuilder(basePath).append(this.getUri());
        if (StringUtils.isNotBlank(path)) {
            uriBuilder.append(URLHelper.repairAbsolutePath(path));
        }
        return new EipIntegrationInterface()
                .setUri(uriBuilder.toString())
                .setModule(module)
                .setName(this.getName() + (StringUtils.isBlank(nameSuffix) ? "" : ("-" + nameSuffix)))
                .setInterfaceName(this.getInterfaceName())
                .construct();
    }
}
