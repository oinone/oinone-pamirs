package pro.shushi.pamirs.framework.connectors.data.datasource.event;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.constant.SystemBeanConstants;
import pro.shushi.pamirs.framework.connectors.data.util.ConfigureUtils;

import static pro.shushi.pamirs.framework.common.constants.ConfigureConstants.SHARDING_RULE_CONFIG_PREFIX;

/**
 * Sharding配置处理类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 3:20 上午
 */
@Component
public class ShardingRefreshEventPreHandler implements RefreshEventHandler {

    @Override
    public boolean needHandle(String changeKey) {
        return changeKey.startsWith(SHARDING_RULE_CONFIG_PREFIX);
    }

    @Override
    public void handle(RefreshContext context, String changeKey) {
        String namespace = ConfigureUtils.substringForNamespace(changeKey, SHARDING_RULE_CONFIG_PREFIX);
        if (StringUtils.isNotBlank(namespace)) {
            context.getRefreshConfSet().add(SystemBeanConstants.SHARDING_RULE_CONFIGURATION);
            context.getRefreshAllSet().add(namespace);
        }
    }

}
