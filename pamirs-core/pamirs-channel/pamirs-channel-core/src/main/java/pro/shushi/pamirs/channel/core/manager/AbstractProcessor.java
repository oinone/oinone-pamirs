package pro.shushi.pamirs.channel.core.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.util.JacksonMapper;

/**
 * AbstractProcessor
 *
 * @author yakir on 2020/04/20 14:53.
 */
abstract
public class AbstractProcessor {

    protected ObjectMapper elasticJsonMapper = JacksonMapper.builder()
            .deDisableFailOnUnknowPropertis()
            .deDisableFailOnIgnoredProperties()
            .seDisableDatesAsTimestamps()
            .seDisableFailOnEmptyBeans()
            .mapper();

    public ObjectMapper elasticDefaultMapper = JacksonMapper.builder()
            .mapper();
}
