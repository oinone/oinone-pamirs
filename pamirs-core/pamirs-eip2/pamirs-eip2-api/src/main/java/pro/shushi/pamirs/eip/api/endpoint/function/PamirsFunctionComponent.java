package pro.shushi.pamirs.eip.api.endpoint.function;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Map;

/**
 * @author drome
 * @date 2021/8/612:38 下午
 */
//@org.apache.camel.spi.annotations.Component("function") 手动注册了,先干掉吧,不清楚
@Slf4j
public class PamirsFunctionComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        PamirsFunctionEndpoint endpoint = new PamirsFunctionEndpoint(uri, this);

        try {
            String[] keys = remaining.split(CharacterConstants.SEPARATOR_COLON);
            String namespace = keys[0];
            String fun = keys[1];
            endpoint.setNamespace(namespace);
            endpoint.setFun(fun);
        } catch (Exception e) {
            log.error("Function interface definition exception, uri:{}", uri, e);
        }
        return endpoint;
    }
}
