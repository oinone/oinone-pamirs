package pro.shushi.pamirs.eip.api.tenant;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipPamirsTenantFinder;

import java.util.Map;
import java.util.Optional;

/**
 * DefaultUrlTenantFinder
 *
 * @author yakir on 2024/07/30 11:11.
 */
@Component
public class DefaultUrlTenantFinder implements IEipPamirsTenantFinder {

    @Override
    public boolean match(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        return true;
    }

    @Override
    public String find(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        String url = Optional.ofNullable(context.getExecutorContext().get("http"))
                .map(_data -> (Map<?, ?>) _data)
                .map(_data -> _data.get("url"))
                .map(_data -> (Map<?, ?>) _data)
                .map(_data -> _data.get("dynamic"))
                .map(_data -> (Map<?, ?>) _data)
                .map(_data -> _data.get("params"))
                .map(_data -> (Map<?, ?>) _data)
                .map(_data -> _data.get("CamelHttpUrl"))
                .map(_data -> (String) _data)
                .filter(StringUtils::isNotBlank)
                .filter(_url -> _url.contains(".oinone.top"))
                .map(_data -> _data.substring(0, _data.lastIndexOf(".oinone.top")))
                .filter(StringUtils::isNotBlank)
                .orElse(null);
        String tenant = null;
        if (StringUtils.contains(url, "://")) {
            tenant = url.substring(url.indexOf("://") + 3);
        }
        return tenant;
    }
}
