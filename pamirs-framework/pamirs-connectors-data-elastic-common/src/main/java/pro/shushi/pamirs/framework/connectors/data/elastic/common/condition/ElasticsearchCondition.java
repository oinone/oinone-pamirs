package pro.shushi.pamirs.framework.connectors.data.elastic.common.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static pro.shushi.pamirs.framework.connectors.data.elastic.common.constant.Constants.ELASTIC_CFG_PROP_PREFIX;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_DOT;

/**
 * ElasticsearchCondition
 *
 * @author yakir on 2022/08/30 15:07.
 */
public class ElasticsearchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return StringUtils.isNotBlank(context.getEnvironment().getProperty(ELASTIC_CFG_PROP_PREFIX + SEPARATOR_DOT + "url"));
    }
}
