package pro.shushi.pamirs.framework.connectors.event.rocketmq.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.marshalling.PamirsMessageJsonConverter;

/**
 * RocketMQMessageConverterFactory
 *
 * @author yakir on 2023/12/11 20:33.
 */
@Configuration
@ConditionalOnMissingBean(PamirsMessageJsonConverter.class)
@Conditional(NotifySwitchCondition.class)
public class RocketMQMessageConverterFactory {

    @Bean
    public PamirsMessageJsonConverter pamirsMessageJsonConverter() {
        return new PamirsMessageJsonConverter();
    }

}